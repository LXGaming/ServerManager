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

package nz.co.lolnet.servermanager.bungee.service;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPool;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.common.service.RedisService;
import nz.co.lolnet.servermanager.common.util.Toolbox;

public class RedisServiceImpl extends RedisService {
    
    private JedisPool jedisPool;
    
    @Override
    public boolean prepareService() {
        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee");
        if (!(plugin instanceof RedisBungee)) {
            ServerManager.getInstance().getLogger().error("RedisBungee is not loaded");
            return false;
        }
        
        setJedisPool(((RedisBungee) plugin).getPool());
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
        
        RedisBungee.getApi().registerPubSubChannels(getChannels().toArray(new String[0]));
    }
    
    @Override
    public void shutdown() {
        if (getJedisPool() == null || getJedisPool().isClosed()) {
            return;
        }
        
        RedisBungee.getApi().unregisterPubSubChannels(getChannels().toArray(new String[0]));
    }
    
    @Override
    public void publish(String channel, String message) {
        RedisBungee.getApi().sendChannelMessage(channel, message);
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