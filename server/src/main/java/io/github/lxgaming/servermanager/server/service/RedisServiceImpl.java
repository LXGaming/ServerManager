/*
 * Copyright 2018 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.servermanager.server.service;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.configuration.Config;
import io.github.lxgaming.servermanager.common.manager.ServiceManager;
import io.github.lxgaming.servermanager.common.service.RedisService;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.configuration.ServerConfig;
import io.github.lxgaming.servermanager.server.listener.RedisListener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisServiceImpl extends RedisService {
    
    private final RedisListener redisListener = new RedisListener();
    private JedisPool jedisPool;
    private int maximumReconnectDelay;
    private int reconnectTimeout = 2;
    
    @Override
    public boolean prepare() {
        ServerManagerImpl.getInstance().getConfig().map(ServerConfig::getRedisCategory).ifPresent(redis -> {
            if (redis.isAutoReconnect()) {
                maximumReconnectDelay = redis.getMaximumReconnectDelay();
            } else {
                maximumReconnectDelay = 0;
            }
            
            if (getJedisPool() == null) {
                JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                jedisPoolConfig.setMaxTotal(redis.getMaximumPoolSize());
                jedisPoolConfig.setMaxIdle(redis.getMaximumIdle());
                jedisPoolConfig.setMinIdle(redis.getMinimumIdle());
                this.jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getPort(), Protocol.DEFAULT_TIMEOUT, redis.getPassword());
            }
        });
        
        return super.prepare() && getJedisPool() != null && !getJedisPool().isClosed();
    }
    
    @Override
    public void execute() throws Exception {
        try (Jedis jedis = getJedisPool().getResource()) {
            ServerManager.getInstance().getConfig()
                    .map(Config::getName)
                    .map(name -> Toolbox.createId(ServerManager.getInstance().getPlatformType(), name))
                    .map(Toolbox::createChannel)
                    .ifPresent(jedis::clientSetname);
            
            ServerManager.getInstance().getLogger().info("Connected to Redis");
            jedis.subscribe(getRedisListener(), getChannels().toArray(new String[0]));
        } catch (JedisConnectionException ex) {
            ServerManager.getInstance().getLogger().warn("Got disconnected from Redis...");
            if (reconnect()) {
                ServiceManager.schedule(this);
            }
        }
    }
    
    private boolean reconnect() throws InterruptedException {
        if (maximumReconnectDelay <= 0) {
            return false;
        }
        
        ServerManager.getInstance().getLogger().warn("Attempting to reconnect in {}", Toolbox.getTimeString(reconnectTimeout * 1000));
        while (!getJedisPool().isClosed()) {
            Thread.sleep(reconnectTimeout * 1000);
            ServerManager.getInstance().getLogger().warn("Attempting to reconnect!");
            
            try (Jedis jedis = getJedisPool().getResource()) {
                reconnectTimeout = 2;
                return true;
            } catch (JedisConnectionException ex) {
                reconnectTimeout = Math.min(reconnectTimeout << 1, maximumReconnectDelay);
                ServerManager.getInstance().getLogger().warn("Reconnect failed! Next attempt in {}", Toolbox.getTimeString(reconnectTimeout * 1000));
            }
        }
        
        return false;
    }
    
    @Override
    public void shutdown() {
        if (getRedisListener() != null && getRedisListener().isSubscribed()) {
            getRedisListener().unsubscribe(getChannels().toArray(new String[0]));
        }
        
        if (getJedisPool() != null && !getJedisPool().isClosed()) {
            getJedisPool().close();
        }
        
        if (isRunning()) {
            getScheduledFuture().cancel(true);
        }
    }
    
    @Override
    public void publish(String channel, String message) {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.publish(channel, message);
        } catch (JedisConnectionException ex) {
        }
    }
    
    @Override
    protected String clientList() {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.clientList();
        } catch (JedisConnectionException ex) {
            return null;
        }
    }
    
    public JedisPool getJedisPool() {
        return jedisPool;
    }
    
    public RedisListener getRedisListener() {
        return redisListener;
    }
}
