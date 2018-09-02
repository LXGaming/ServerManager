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

import nz.co.lolnet.servermanager.common.AbstractServerManager;
import nz.co.lolnet.servermanager.common.managers.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Logger;
import nz.co.lolnet.servermanager.common.util.Reference;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.configuration.Config;
import nz.co.lolnet.servermanager.server.configuration.Configuration;
import nz.co.lolnet.servermanager.server.managers.CommandManager;
import nz.co.lolnet.servermanager.server.services.RedisService;
import nz.co.lolnet.servermanager.server.util.NetworkHandler;
import nz.co.lolnet.servermanager.server.util.ShutdownHook;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager extends AbstractServerManager {
    
    private final RedisService redisService;
    private final AtomicBoolean running;
    
    public ServerManager() {
        this.logger = new Logger();
        this.path = Toolbox.getPath().orElse(null);
        this.networkHandler = new NetworkHandler();
        this.configuration = new Configuration();
        this.redisService = new RedisService();
        this.running = new AtomicBoolean(false);
    }
    
    public void loadServerManager() {
        super.loadServerManager();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        reloadLogger();
        CommandManager.buildCommands();
        ServiceManager.schedule(getRedisService());
        getRunning().set(true);
    }
    
    public void reloadLogger() {
        if (getConfig().map(Config::isDebug).orElse(false)) {
            Configurator.setLevel(Reference.ID, Level.DEBUG);
            getLogger().debug("Debug mode enabled");
        } else {
            Configurator.setLevel(Reference.ID, Level.INFO);
            getLogger().info("Debug mode disabled");
        }
    }
    
    public static ServerManager getInstance() {
        return (ServerManager) AbstractServerManager.getInstance();
    }
    
    @Override
    public Configuration getConfiguration() {
        return (Configuration) configuration;
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
    
    public AtomicBoolean getRunning() {
        return running;
    }
}