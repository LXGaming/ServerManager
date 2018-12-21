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

package nz.co.lolnet.servermanager.server;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.Platform;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.LoggerImpl;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.configuration.ServerConfig;
import nz.co.lolnet.servermanager.server.configuration.ServerConfiguration;
import nz.co.lolnet.servermanager.server.manager.CommandManager;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;
import nz.co.lolnet.servermanager.server.service.RedisServiceImpl;
import nz.co.lolnet.servermanager.server.util.NetworkHandlerImpl;
import nz.co.lolnet.servermanager.server.util.ShutdownHook;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManagerImpl extends ServerManager {
    
    private final RedisServiceImpl redisService;
    private final AtomicBoolean running;
    
    public ServerManagerImpl() {
        this.logger = new LoggerImpl();
        this.path = Toolbox.getPath().orElse(null);
        this.configuration = new ServerConfiguration();
        this.platformType = Platform.Type.SERVER;
        this.redisService = new RedisServiceImpl();
        this.running = new AtomicBoolean(false);
    }
    
    @Override
    public void loadServerManager() {
        getLogger().info("Initializing...");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        getConfiguration().loadConfiguration();
        CommandManager.buildCommands();
        ConnectionManager.buildConnections();
        PacketManager.buildPackets();
        registerNetworkHandler(NetworkHandlerImpl.class);
        ServiceManager.schedule(getRedisService());
        getConfiguration().saveConfiguration();
        getRunning().set(true);
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
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return PacketManager.registerNetworkHandler(networkHandlerClass);
    }
    
    @Override
    public void sendPacket(String channel, Packet packet) {
        PacketManager.sendPacket(channel, packet, getRedisService()::publish);
    }
    
    @Override
    public void sendPacket(Packet packet) {
        PacketManager.sendPacket(packet, getRedisService()::publish);
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    @Override
    public Optional<? extends ServerConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable((ServerConfig) getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisServiceImpl getRedisService() {
        return redisService;
    }
    
    public AtomicBoolean getRunning() {
        return running;
    }
}
