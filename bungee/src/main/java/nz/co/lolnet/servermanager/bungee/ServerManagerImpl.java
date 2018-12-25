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

package nz.co.lolnet.servermanager.bungee;

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.util.Logger;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.bungee.configuration.BungeeConfig;
import nz.co.lolnet.servermanager.bungee.configuration.BungeeConfiguration;
import nz.co.lolnet.servermanager.bungee.service.RedisServiceImpl;
import nz.co.lolnet.servermanager.bungee.util.NetworkHandlerImpl;
import nz.co.lolnet.servermanager.common.configuration.Configuration;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.service.RedisService;
import nz.co.lolnet.servermanager.common.util.LoggerImpl;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private Configuration configuration;
    private RedisService redisService;
    
    private ServerManagerImpl() {
        this.platformType = Platform.Type.BUNGEECORD;
        this.logger = new LoggerImpl();
        this.configuration = new BungeeConfiguration(BungeePlugin.getInstance().getDataFolder().toPath());
        this.redisService = new RedisServiceImpl();
    }
    
    public static boolean init() {
        if (getInstance() != null) {
            return false;
        }
        
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger()
                .add(Logger.Level.INFO, BungeePlugin.getInstance().getLogger()::info)
                .add(Logger.Level.WARN, BungeePlugin.getInstance().getLogger()::warning)
                .add(Logger.Level.ERROR, BungeePlugin.getInstance().getLogger()::severe)
                .add(Logger.Level.DEBUG, BungeePlugin.getInstance().getLogger()::info);
        
        serverManager.loadServerManager();
        serverManager.reloadServerManager();
        return true;
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
        if (getConfig().map(Config::isDebug).orElse(false)) {
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
                .map(BungeeConfig::getHost)
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
    
    public Optional<BungeeConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable((BungeeConfig) getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    private RedisService getRedisService() {
        return redisService;
    }
}