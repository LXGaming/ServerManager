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

package nz.co.lolnet.servermanager.server.service;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.common.service.RedisService;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.configuration.ServerConfig;
import nz.co.lolnet.servermanager.server.configuration.category.RedisCategory;
import nz.co.lolnet.servermanager.server.listener.RedisListener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisServiceImpl extends RedisService {
    
    private JedisPool jedisPool;
    
    @Override
    public boolean prepareService() {
        RedisCategory redisCategory = ServerManagerImpl.getInstance().getConfig().map(ServerConfig::getRedisCategory).orElse(null);
        if (redisCategory == null) {
            return false;
        }
        
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        setJedisPool(new JedisPool(jedisPoolConfig, redisCategory.getHost(), redisCategory.getPort(), Protocol.DEFAULT_TIMEOUT, redisCategory.getPassword()));
        return super.prepareService();
    }
    
    @Override
    public void executeService() {
        try (Jedis jedis = getJedisPool().getResource()) {
            ServerManager.getInstance().getConfig()
                    .map(Config::getName)
                    .map(name -> Toolbox.createChannel(ServerManager.getInstance().getPlatformType(), name))
                    .ifPresent(jedis::clientSetname);
            
            jedis.subscribe(new RedisListener(), getChannels().toArray(new String[0]));
        }
    }
    
    @Override
    public void shutdown() {
        if (getJedisPool() == null || getJedisPool().isClosed()) {
            return;
        }
        
        getJedisPool().close();
        getJedisPool().destroy();
    }
    
    @Override
    public void publish(String channel, String message) {
        try (Jedis jedis = getJedisPool().getResource()) {
            jedis.publish(channel, message);
        }
    }
    
    @Override
    public String clientList() {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.clientList();
        }
    }
    
    public JedisPool getJedisPool() {
        return jedisPool;
    }
    
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
