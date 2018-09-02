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

import nz.co.lolnet.servermanager.bungee.configuration.Config;
import nz.co.lolnet.servermanager.bungee.configuration.Configuration;
import nz.co.lolnet.servermanager.bungee.services.RedisService;
import nz.co.lolnet.servermanager.bungee.util.NetworkHandler;
import nz.co.lolnet.servermanager.common.AbstractServerManager;
import nz.co.lolnet.servermanager.common.managers.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Logger;

import java.util.Optional;

public class ServerManager extends AbstractServerManager {
    
    private final Configuration configuration;
    private final RedisService redisService;
    
    public ServerManager() {
        this.logger = new Logger();
        this.path = BungeePlugin.getInstance().getDataFolder().toPath();
        this.networkHandler = new NetworkHandler();
        this.configuration = new Configuration();
        this.redisService = new RedisService();
    }
    
    @Override
    public void loadServerManager() {
        super.loadServerManager();
        ServiceManager.schedule(getRedisService());
    }
    
    public static ServerManager getInstance() {
        return (ServerManager) AbstractServerManager.getInstance();
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Optional<? extends Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisService getRedisService() {
        return redisService;
    }
}