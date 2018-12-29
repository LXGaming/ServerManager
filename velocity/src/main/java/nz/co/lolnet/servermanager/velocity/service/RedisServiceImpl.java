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

package nz.co.lolnet.servermanager.velocity.service;

import nz.co.lolnet.redisvelocity.api.RedisVelocity;
import nz.co.lolnet.redisvelocity.lib.jedis.Jedis;
import nz.co.lolnet.redisvelocity.lib.jedis.JedisPool;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.common.service.RedisService;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.velocity.VelocityPlugin;

public class RedisServiceImpl extends RedisService {
    
    private JedisPool jedisPool;
    
    @Override
    public boolean prepareService() {
        if (!VelocityPlugin.getInstance().getProxy().getPluginManager().isLoaded("redisvelocity")) {
            ServerManager.getInstance().getLogger().error("RedisVelocity is not loaded");
            return false;
        }
        
        setJedisPool(nz.co.lolnet.redisvelocity.plugin.VelocityPlugin.getInstance().getRedisService().getJedisPool());
        return super.prepareService();
    }
    
    @Override
    public void executeService() {
        try (Jedis jedis = getJedisPool().getResource()) {
            ServerManager.getInstance().getConfig()
                    .map(Config::getName)
                    .map(name -> Toolbox.createChannel(ServerManager.getInstance().getPlatformType(), name))
                    .ifPresent(jedis::clientSetname);
        }
        
        RedisVelocity.getInstance().registerChannels(getChannels().toArray(new String[0]));
    }
    
    @Override
    public void shutdown() {
        RedisVelocity.getInstance().unregisterChannels(getChannels().toArray(new String[0]));
    }
    
    @Override
    public void publish(String channel, String message) {
        RedisVelocity.getInstance().sendMessage(channel, message);
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
}