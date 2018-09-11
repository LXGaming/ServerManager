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

import nz.co.lolnet.servermanager.api.network.packet.AbstractPacket;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.service.AbstractService;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.configuration.Config;
import nz.co.lolnet.servermanager.server.configuration.category.RedisCategory;
import nz.co.lolnet.servermanager.server.listener.RedisListener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.Optional;
import java.util.Set;

public class RedisService extends AbstractService {
    
    private final Set<String> channels = Toolbox.newHashSet();
    private JedisPool jedisPool;
    
    @Override
    public boolean prepareService() {
        getChannels().add(Reference.ID + "-data");
        getProxyChannel().ifPresent(getChannels()::add);
        
        RedisCategory redisCategory = ServerManagerImpl.getInstance().getConfig().map(Config::getRedisCategory).orElse(null);
        if (redisCategory == null) {
            return false;
        }
        
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        setJedisPool(new JedisPool(jedisPoolConfig, redisCategory.getHost(), redisCategory.getPort(), Protocol.DEFAULT_TIMEOUT, redisCategory.getPassword()));
        return true;
    }
    
    @Override
    public void executeService() {
        getJedisPool().getResource().subscribe(new RedisListener(), getChannels().toArray(new String[0]));
    }
    
    public void publish(String channel, AbstractPacket packet) {
        if (Toolbox.isBlank(packet.getSender())) {
            getProxyName().map(name -> "data" + name).ifPresent(packet::setSender);
        }
        
        if (Toolbox.isBlank(packet.getReplyTo())) {
            getProxyChannel().ifPresent(packet::setReplyTo);
        }
        
        try (Jedis jedis = getJedisPool().getResource()) {
            PacketManager.sendPacket(packet, data -> jedis.publish(channel, data));
        }
    }
    
    public Optional<String> getProxyChannel() {
        return getProxyName().map(name -> Reference.ID + "-data-" + name);
    }
    
    public Optional<String> getProxyName() {
        return ServerManagerImpl.getInstance().getConfig().map(Config::getProxyName).filter(Toolbox::isNotBlank).map(String::toLowerCase);
    }
    
    public Set<String> getChannels() {
        return channels;
    }
    
    public JedisPool getJedisPool() {
        return jedisPool;
    }
    
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}