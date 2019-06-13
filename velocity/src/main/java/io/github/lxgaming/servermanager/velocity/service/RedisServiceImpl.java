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

package io.github.lxgaming.servermanager.velocity.service;

import io.github.lxgaming.redisvelocity.api.RedisVelocity;
import io.github.lxgaming.redisvelocity.lib.jedis.Jedis;
import io.github.lxgaming.redisvelocity.lib.jedis.JedisPool;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.configuration.Config;
import io.github.lxgaming.servermanager.common.service.RedisService;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.velocity.VelocityPlugin;

public class RedisServiceImpl extends RedisService {
    
    private JedisPool jedisPool;
    
    @Override
    public boolean prepare() {
        if (!VelocityPlugin.getInstance().getProxy().getPluginManager().isLoaded("redisvelocity")) {
            ServerManager.getInstance().getLogger().error("RedisVelocity is not loaded");
            return false;
        }
        
        setJedisPool(io.github.lxgaming.redisvelocity.plugin.VelocityPlugin.getInstance().getRedisService().getJedisPool());
        return super.prepare();
    }
    
    @Override
    public void execute() {
        try (Jedis jedis = getJedisPool().getResource()) {
            ServerManager.getInstance().getConfig()
                    .map(Config::getName)
                    .map(name -> Toolbox.createId(ServerManager.getInstance().getPlatformType(), name))
                    .map(Toolbox::createChannel)
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