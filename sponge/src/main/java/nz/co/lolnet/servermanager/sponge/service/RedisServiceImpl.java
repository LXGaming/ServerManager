/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.servermanager.sponge.service;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.common.service.RedisService;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.sponge.ServerManagerImpl;
import nz.co.lolnet.servermanager.sponge.configuration.SpongeConfig;
import nz.co.lolnet.servermanager.sponge.configuration.category.RedisCategory;
import nz.co.lolnet.servermanager.sponge.listener.RedisListener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisServiceImpl extends RedisService {
    
    private JedisPool jedisPool;
    private RedisListener redisListener;
    
    @Override
    public boolean prepareService() {
        RedisCategory redisCategory = ServerManagerImpl.getInstance().getConfig().map(SpongeConfig::getRedisCategory).orElse(null);
        if (redisCategory == null) {
            return false;
        }
        
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        setJedisPool(new JedisPool(jedisPoolConfig, redisCategory.getHost(), redisCategory.getPort(), Protocol.DEFAULT_TIMEOUT, redisCategory.getPassword()));
        setRedisListener(new RedisListener());
        return super.prepareService();
    }
    
    @Override
    public void executeService() {
        try (Jedis jedis = getJedisPool().getResource()) {
            ServerManager.getInstance().getConfig()
                    .map(Config::getName)
                    .map(name -> Toolbox.createChannel(ServerManager.getInstance().getPlatformType(), name))
                    .ifPresent(jedis::clientSetname);
            
            jedis.subscribe(getRedisListener(), getChannels().toArray(new String[0]));
        }
    }
    
    @Override
    public void shutdown() {
        if (getRedisListener() != null && getRedisListener().isSubscribed()) {
            getRedisListener().unsubscribe(getChannels().toArray(new String[0]));
        }
        
        if (getJedisPool() != null && !getJedisPool().isClosed()) {
            getJedisPool().close();
        }
    }
    
    @Override
    public void publish(String channel, String message) {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.publish(channel, message);
        }
    }
    
    @Override
    protected String clientList() {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.clientList();
        }
    }
    
    public JedisPool getJedisPool() {
        return jedisPool;
    }
    
    private void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public RedisListener getRedisListener() {
        return redisListener;
    }
    
    private void setRedisListener(RedisListener redisListener) {
        this.redisListener = redisListener;
    }
}