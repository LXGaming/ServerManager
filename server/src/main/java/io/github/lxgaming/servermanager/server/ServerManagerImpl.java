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

package io.github.lxgaming.servermanager.server;

import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.network.NetworkHandler;
import io.github.lxgaming.servermanager.api.network.Packet;
import io.github.lxgaming.servermanager.api.util.Logger;
import io.github.lxgaming.servermanager.api.util.Reference;
import io.github.lxgaming.servermanager.common.manager.PacketManager;
import io.github.lxgaming.servermanager.common.manager.ServiceManager;
import io.github.lxgaming.servermanager.common.util.LoggerImpl;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.configuration.ServerConfig;
import io.github.lxgaming.servermanager.server.configuration.ServerConfiguration;
import io.github.lxgaming.servermanager.server.handler.RequestNetworkHandler;
import io.github.lxgaming.servermanager.server.handler.ResponseNetworkHandler;
import io.github.lxgaming.servermanager.server.manager.CommandManager;
import io.github.lxgaming.servermanager.server.manager.ConnectionManager;
import io.github.lxgaming.servermanager.server.service.RedisServiceImpl;
import io.github.lxgaming.servermanager.server.service.StatusService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private ServerConfiguration configuration;
    private RedisServiceImpl redisService;
    private volatile boolean running;
    
    private ServerManagerImpl() {
        this.platformType = Platform.Type.SERVER;
        this.logger = new LoggerImpl();
        this.configuration = new ServerConfiguration(Toolbox.getPath().orElse(null));
        this.redisService = new RedisServiceImpl();
    }
    
    public static boolean init() {
        if (getInstance() != null) {
            return false;
        }
        
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger()
                .add(Logger.Level.INFO, LogManager.getLogger(Reference.ID)::info)
                .add(Logger.Level.WARN, LogManager.getLogger(Reference.ID)::warn)
                .add(Logger.Level.ERROR, LogManager.getLogger(Reference.ID)::error)
                .add(Logger.Level.DEBUG, LogManager.getLogger(Reference.ID)::debug);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Thread.currentThread().setName("Shutdown Thread");
            ServerManager.getInstance().getLogger().info("Shutting down...");
            ServerManagerImpl.getInstance().shutdownServerManager();
            LogManager.shutdown();
        }));
        
        serverManager.loadServerManager();
        return true;
    }
    
    @Override
    public void loadServerManager() {
        getLogger().info("Initializing...");
        getConfiguration().loadConfiguration();
        reloadServerManager();
        CommandManager.buildCommands();
        ConnectionManager.buildConnections();
        PacketManager.buildPackets();
        registerNetworkHandler(RequestNetworkHandler.class);
        registerNetworkHandler(ResponseNetworkHandler.class);
        ServiceManager.schedule(getRedisService());
        ServiceManager.schedule(new StatusService());
        getConfiguration().saveConfiguration();
        setRunning(true);
        getLogger().info("{} v{} has loaded", Reference.NAME, Reference.VERSION);
    }
    
    @Override
    public void reloadServerManager() {
        if (getConfig().map(ServerConfig::isDebug).orElse(false)) {
            Configurator.setLevel(Reference.ID, Level.DEBUG);
            getLogger().debug("Debug mode enabled");
        } else {
            Configurator.setLevel(Reference.ID, Level.INFO);
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
    
    public void sendRequest(String id, Packet packet) {
        packet.setSender(null);
        packet.setType(Packet.Type.REQUEST);
        sendPacket(id, packet);
    }
    
    public void sendResponse(String id, Packet packet) {
        packet.setSender(null);
        packet.setType(Packet.Type.RESPONSE);
        sendPacket(id, packet);
    }
    
    public void sendPacket(String id, Packet packet) {
        PacketManager.sendPacket(id, packet, getRedisService()::publish);
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    public ServerConfiguration getConfiguration() {
        return configuration;
    }
    
    public Optional<ServerConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisServiceImpl getRedisService() {
        return redisService;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
}