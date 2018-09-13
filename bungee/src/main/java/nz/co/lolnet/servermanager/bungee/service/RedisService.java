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
import nz.co.lolnet.servermanager.api.data.Platform;
import nz.co.lolnet.servermanager.api.network.packet.AbstractPacket;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.bungee.ServerManagerImpl;
import nz.co.lolnet.servermanager.bungee.configuration.Config;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.service.AbstractService;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.util.Optional;
import java.util.Set;

public class RedisService extends AbstractService {
    
    private final Set<String> channels = Toolbox.newHashSet();
    
    @Override
    public boolean prepareService() {
        getChannels().add(Reference.ID + "-" + Platform.Type.BUNGEE);
        getServerChannel().ifPresent(getChannels()::add);
        return true;
    }
    
    @Override
    public void executeService() {
        RedisBungee.getApi().registerPubSubChannels(getChannels().toArray(new String[0]));
    }
    
    public void publish(AbstractPacket packet) {
        String channel;
        if (Toolbox.isNotBlank(packet.getReplyTo())) {
            channel = packet.getReplyTo();
        } else {
            channel = getProxyChannel().orElse(null);
        }
        
        packet.setSender(null);
        packet.setReplyTo(null);
        publish(channel, packet);
    }
    
    public void publish(String channel, AbstractPacket packet) {
        if (Toolbox.isBlank(packet.getSender())) {
            getServerName().map(name -> Platform.Type.BUNGEE + name).ifPresent(packet::setSender);
        }
        
        if (Toolbox.isBlank(packet.getReplyTo())) {
            getServerChannel().ifPresent(packet::setReplyTo);
        }
        
        PacketManager.sendPacket(packet, data -> RedisBungee.getApi().sendChannelMessage(channel, data));
    }
    
    public Optional<String> getProxyChannel() {
        return getProxyName().map(name -> Reference.ID + "-data-" + name);
    }
    
    public Optional<String> getServerChannel() {
        return getServerName().map(name -> Reference.ID + "-" + Platform.Type.BUNGEE + "-" + name);
    }
    
    public Optional<String> getProxyName() {
        return ServerManagerImpl.getInstance().getConfig().map(Config::getProxyName).filter(Toolbox::isNotBlank).map(String::toLowerCase);
    }
    
    public Optional<String> getServerName() {
        return ServerManagerImpl.getInstance().getConfig().map(Config::getServerName).filter(Toolbox::isNotBlank).map(String::toLowerCase);
    }
    
    public Set<String> getChannels() {
        return channels;
    }
}