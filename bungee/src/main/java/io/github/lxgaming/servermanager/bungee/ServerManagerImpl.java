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

package io.github.lxgaming.servermanager.bungee;

import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.configuration.Config;
import io.github.lxgaming.servermanager.api.network.NetworkHandler;
import io.github.lxgaming.servermanager.api.network.Packet;
import io.github.lxgaming.servermanager.api.util.Logger;
import io.github.lxgaming.servermanager.api.util.Reference;
import io.github.lxgaming.servermanager.bungee.configuration.BungeeConfig;
import io.github.lxgaming.servermanager.bungee.configuration.BungeeConfiguration;
import io.github.lxgaming.servermanager.bungee.service.RedisServiceImpl;
import io.github.lxgaming.servermanager.bungee.util.NetworkHandlerImpl;
import io.github.lxgaming.servermanager.common.manager.PacketManager;
import io.github.lxgaming.servermanager.common.manager.ServiceManager;
import io.github.lxgaming.servermanager.common.util.LoggerImpl;
import io.github.lxgaming.servermanager.common.util.Toolbox;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private BungeeConfiguration configuration;
    private RedisServiceImpl redisService;
    
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
                .add(Logger.Level.DEBUG, message -> {
                    if (ServerManager.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        BungeePlugin.getInstance().getLogger().info(message);
                    }
                });
        
        serverManager.loadServerManager();
        return true;
    }
    
    @Override
    public void loadServerManager() {
        getLogger().info("Initializing...");
        getConfiguration().loadConfiguration();
        reloadServerManager();
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
    public void shutdownServerManager() {
        getRedisService().shutdown();
        ServiceManager.shutdown();
    }
    
    @Override
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return PacketManager.registerNetworkHandler(networkHandlerClass);
    }
    
    @Override
    public void sendPacket(Packet packet) {
        PacketManager.sendPacket(Toolbox.createId(Platform.Type.SERVER), packet, getRedisService()::publish);
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    public BungeeConfiguration getConfiguration() {
        return configuration;
    }
    
    public Optional<BungeeConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisServiceImpl getRedisService() {
        return redisService;
    }
}