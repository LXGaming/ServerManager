/*
 * Copyright 2021 Alex Thomson
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

package io.github.lxgaming.servermanager.client;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.client.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.client.configuration.ConfigurationImpl;
import io.github.lxgaming.servermanager.client.manager.NetworkManager;
import io.github.lxgaming.servermanager.common.configuration.Config;
import io.github.lxgaming.servermanager.common.configuration.category.GeneralCategory;
import io.github.lxgaming.servermanager.common.event.EventManagerImpl;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    private final Logger logger;
    private final ConfigurationImpl configuration;
    
    ServerManagerImpl() {
        this(Toolbox.getPath());
    }
    
    public ServerManagerImpl(Path path) {
        super();
        this.eventManager = new EventManagerImpl();
        this.logger = LoggerFactory.getLogger(ServerManager.NAME);
        this.configuration = new ConfigurationImpl(path);
    }
    
    public void load() {
        getLogger().info("Initializing...");
        if (!reload()) {
            getLogger().error("Failed to load");
            return;
        }
        
        NetworkManager.prepare();
        
        getConfiguration().saveConfiguration();
        
        NetworkManager.execute();
        
        getLogger().info("{} v{} has loaded", ServerManager.NAME, ServerManager.VERSION);
    }
    
    public boolean reload() {
        getConfiguration().loadConfiguration();
        if (!getConfig().isPresent()) {
            return false;
        }
        
        getConfiguration().saveConfiguration();
        reloadLogger();
        
        return true;
    }
    
    public void reloadLogger() {
        if (getConfig().map(Config::getGeneralCategory).map(GeneralCategory::isDebug).orElse(false)) {
            getLogger().debug("Debug mode enabled");
        } else {
            getLogger().info("Debug mode disabled");
        }
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public ConfigurationImpl getConfiguration() {
        return configuration;
    }
    
    public Optional<ConfigImpl> getConfig() {
        return Optional.ofNullable(getConfiguration().getConfig());
    }
}