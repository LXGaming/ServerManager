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

package io.github.lxgaming.servermanager.server;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.common.configuration.Config;
import io.github.lxgaming.servermanager.common.configuration.category.GeneralCategory;
import io.github.lxgaming.servermanager.common.entity.InstanceImpl;
import io.github.lxgaming.servermanager.common.manager.InstanceManager;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.configuration.ConfigurationImpl;
import io.github.lxgaming.servermanager.server.configuration.category.InstanceCategory;
import io.github.lxgaming.servermanager.server.manager.CommandManager;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import io.github.lxgaming.servermanager.server.manager.TaskManager;
import io.github.lxgaming.servermanager.server.util.ShutdownHook;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManagerImpl extends ServerManager {
    
    private final Logger logger;
    private final ConfigurationImpl configuration;
    private final Instant startTime;
    private final AtomicBoolean state;
    
    public ServerManagerImpl() {
        this(Toolbox.getPath());
    }
    
    private ServerManagerImpl(Path path) {
        super();
        this.logger = LoggerFactory.getLogger(ServerManager.NAME);
        this.configuration = new ConfigurationImpl(path);
        this.startTime = Instant.now();
        this.state = new AtomicBoolean(false);
    }
    
    public void load() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        getLogger().info("Initializing...");
        if (!reload()) {
            getLogger().error("Failed to load");
            return;
        }
        
        CommandManager.prepare();
        NetworkManager.prepare();
        TaskManager.prepare();
        
        getConfiguration().saveConfiguration();
        
        NetworkManager.execute();
        
        Set<InstanceCategory> instanceCategories = getConfig().map(ConfigImpl::getInstanceCategories).orElseThrow(NullPointerException::new);
        for (InstanceCategory instanceCategory : instanceCategories) {
            InstanceManager.INSTANCES.add(new InstanceImpl(instanceCategory.getId(), instanceCategory.getName()));
        }
        
        getLogger().info("{} v{} has loaded", ServerManager.NAME, ServerManager.VERSION);
    }
    
    public boolean reload() {
        if (!getConfiguration().loadConfiguration()) {
            return false;
        }
        
        getConfiguration().saveConfiguration();
        reloadLogger();
        
        return true;
    }
    
    public void reloadLogger() {
        if (getConfig().map(Config::getGeneralCategory).map(GeneralCategory::isDebug).orElse(false)) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
            Configurator.setLevel(getLogger().getName(), Level.DEBUG);
            getLogger().debug("Debug mode enabled");
        } else {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
            Configurator.setLevel(getLogger().getName(), Level.INFO);
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
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public AtomicBoolean getState() {
        return state;
    }
}