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

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.bungee.configuration.BungeeConfiguration;
import nz.co.lolnet.servermanager.bungee.configuration.Config;
import nz.co.lolnet.servermanager.bungee.service.RedisService;
import nz.co.lolnet.servermanager.bungee.util.NetworkHandlerImpl;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.LoggerImpl;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private final RedisService redisService;
    
    public ServerManagerImpl() {
        this.logger = new LoggerImpl();
        this.path = BungeePlugin.getInstance().getDataFolder().toPath();
        this.configuration = new BungeeConfiguration();
        this.networkHandler = new NetworkHandlerImpl();
        this.redisService = new RedisService();
    }
    
    @Override
    public void loadServerManager() {
        getLogger().info("Initializing...");
        getConfiguration().loadConfiguration();
        PacketManager.buildPackets();
        ServiceManager.schedule(getRedisService());
        getConfiguration().saveConfiguration();
        getLogger().info("{} v{} has loaded", Reference.NAME, Reference.VERSION);
    }
    
    @Override
    public void reloadServerManager() {
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    public Optional<? extends Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(((BungeeConfiguration) getConfiguration()).getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisService getRedisService() {
        return redisService;
    }
}