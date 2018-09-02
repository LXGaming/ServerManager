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

import nz.co.lolnet.servermanager.common.AbstractServerManager;
import nz.co.lolnet.servermanager.common.managers.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Logger;
import nz.co.lolnet.servermanager.sponge.configuration.Config;
import nz.co.lolnet.servermanager.sponge.configuration.Configuration;
import nz.co.lolnet.servermanager.sponge.services.RedisService;
import nz.co.lolnet.servermanager.sponge.util.NetworkHandler;

import java.util.Optional;

public class ServerManager extends AbstractServerManager {
    
    private final RedisService redisService;
    
    public ServerManager() {
        this.logger = new Logger();
        this.path = SpongePlugin.getInstance().getPath();
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
    
    @Override
    public Configuration getConfiguration() {
        return (Configuration) configuration;
    }
    
    public Optional<Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisService getRedisService() {
        return redisService;
    }
}