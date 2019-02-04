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
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.api.data.Setting;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.util.Logger;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.LoggerImpl;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.sponge.configuration.SpongeConfig;
import nz.co.lolnet.servermanager.sponge.configuration.SpongeConfiguration;
import nz.co.lolnet.servermanager.sponge.service.RedisServiceImpl;
import nz.co.lolnet.servermanager.sponge.service.SpongeService;
import nz.co.lolnet.servermanager.sponge.util.NetworkHandlerImpl;
import org.slf4j.LoggerFactory;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.launch.SpongeLaunch;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private SpongeConfiguration configuration;
    private RedisServiceImpl redisService;
    private Setting setting;
    
    private ServerManagerImpl() {
        this.platformType = Platform.Type.SPONGE;
        this.logger = new LoggerImpl();
        this.configuration = new SpongeConfiguration(SpongeLaunch.getConfigDir().resolve(Reference.ID));
        this.redisService = new RedisServiceImpl();
    }
    
    public static boolean init() {
        if (getInstance() != null) {
            return false;
        }
        
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger()
                .add(Logger.Level.INFO, LoggerFactory.getLogger(Reference.NAME)::info)
                .add(Logger.Level.WARN, LoggerFactory.getLogger(Reference.NAME)::warn)
                .add(Logger.Level.ERROR, LoggerFactory.getLogger(Reference.NAME)::error)
                .add(Logger.Level.DEBUG, message -> {
                    if (ServerManager.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        LoggerFactory.getLogger(Reference.NAME).debug(message);
                    }
                });
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Thread.currentThread().setName("Shutdown Thread");
            ServerManager.getInstance().getLogger().info("Shutting down...");
            
            StatePacket packet = new StatePacket();
            packet.setState(Platform.State.JVM_STOPPED);
            ServerManager.getInstance().sendResponse(packet);
            
            ServerManagerImpl.getInstance().shutdownServerManager();
        }));
        
        serverManager.loadServerManager();
        
        if (!SpongeImpl.isInitialized()) {
            StatePacket packet = new StatePacket();
            packet.setState(Platform.State.JVM_STARTED);
            ServerManager.getInstance().sendResponse(packet);
        }
        
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
        ServiceManager.schedule(new SpongeService());
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
    
    public SpongeConfiguration getConfiguration() {
        return configuration;
    }
    
    public Optional<SpongeConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisServiceImpl getRedisService() {
        return redisService;
    }
    
    public Setting getSetting() {
        return setting;
    }
    
    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}