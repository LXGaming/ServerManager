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

public class ServerManagerImpl extends ServerManager {
    
    private Configuration configuration;
    private RedisService redisService;
    private volatile boolean running;
    
    public ServerManagerImpl() {
        this.platformType = Platform.Type.SERVER;
        this.logger = new LoggerImpl();
        this.configuration = new ServerConfiguration(Toolbox.getPath().orElse(null));
        this.redisService = new RedisServiceImpl();
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
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return PacketManager.registerNetworkHandler(networkHandlerClass);
    }
    
    @Override
    public void sendPacket(Packet packet) {
        throw new UnsupportedOperationException("Not supported");
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
    
    public Optional<ServerConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable((ServerConfig) getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisService getRedisService() {
        return redisService;
    }
    
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
}
