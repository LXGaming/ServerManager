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

package nz.co.lolnet.servermanager.sponge;

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.common.configuration.Configuration;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.service.RedisService;
import nz.co.lolnet.servermanager.common.util.LoggerImpl;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.sponge.configuration.SpongeConfig;
import nz.co.lolnet.servermanager.sponge.configuration.SpongeConfiguration;
import nz.co.lolnet.servermanager.sponge.service.RedisServiceImpl;
import nz.co.lolnet.servermanager.sponge.util.NetworkHandlerImpl;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private Configuration configuration;
    private RedisService redisService;
    
    public ServerManagerImpl() {
        this.platformType = Platform.Type.SPONGE;
        this.logger = new LoggerImpl();
        this.configuration = new SpongeConfiguration(SpongePlugin.getInstance().getPath());
        this.redisService = new RedisServiceImpl();
    }
    
    @Override
    public void loadServerManager() {
        getLogger().info("Initializing...");
        getConfiguration().loadConfiguration();
        PacketManager.buildPackets();
        registerNetworkHandler(NetworkHandlerImpl.class);
        ServiceManager.schedule(getRedisService());
        getConfiguration().saveConfiguration();
        getLogger().info("{} v{} has loaded", Reference.NAME, Reference.VERSION);
    }
    
    @Override
    public void reloadServerManager() {
        if (getConfig().map(SpongeConfig::isDebug).orElse(false)) {
            getLogger().debug("Debug mode enabled");
        } else {
            getLogger().info("Debug mode disabled");
        }
    }
    
    @Override
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return PacketManager.registerNetworkHandler(networkHandlerClass);
    }
    
    @Override
    public void sendPacket(Packet packet) {
        getConfig()
                .map(SpongeConfig::getHost)
                .map(name -> Toolbox.createChannel(Platform.Type.SERVER, name))
                .ifPresent(channel -> sendPacket(channel, packet));
    }
    
    @Override
    public void sendPacket(String channel, Packet packet) {
        PacketManager.sendPacket(channel, packet, getRedisService()::publish);
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Optional<SpongeConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable((SpongeConfig) getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisService getRedisService() {
        return redisService;
    }
}
