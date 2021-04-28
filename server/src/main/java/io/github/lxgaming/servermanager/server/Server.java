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
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.configuration.Config;
import io.github.lxgaming.servermanager.common.configuration.category.GeneralCategory;
import io.github.lxgaming.servermanager.common.event.lifecycle.LifecycleEventImpl;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.configuration.ConfigurationImpl;
import io.github.lxgaming.servermanager.server.listener.InstanceListener;
import io.github.lxgaming.servermanager.server.manager.CommandManager;
import io.github.lxgaming.servermanager.server.manager.IntegrationManager;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import io.github.lxgaming.servermanager.server.util.ShutdownHook;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.core.config.Configurator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Server {
    
    private static Server instance;
    private final Logger logger;
    private final ConfigurationImpl configuration;
    private final AtomicBoolean state;
    private final Instant startTime;
    
    public Server(@NonNull Path path) {
        instance = this;
        this.logger = LoggerFactory.getLogger(Server.class);
        this.configuration = new ConfigurationImpl(path);
        this.state = new AtomicBoolean(false);
        this.startTime = Instant.now();
    }
    
    public boolean prepare() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        if (!getConfiguration().loadConfiguration()) {
            getLogger().error("Failed to load");
            return false;
        }
        
        reloadLogger();
        
        CommandManager.prepare();
        IntegrationManager.prepare();
        NetworkManager.prepare();
        
        ServerManager.getInstance().getEventManager().register(new InstanceListener());
        ServerManager.getInstance().getEventManager().fire(new LifecycleEventImpl.Initialize(Platform.SERVER)).join();
        
        getConfiguration().saveConfiguration();
        getState().set(true);
        return true;
    }
    
    public void execute() {
        IntegrationManager.execute();
        NetworkManager.execute();
    }
    
    public void reloadLogger() {
        if (getConfig().map(Config::getGeneralCategory).map(GeneralCategory::isDebug).orElse(false)) {
            System.setProperty("servermanager.logging.console.level", "DEBUG");
            Configurator.reconfigure();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
            getLogger().debug("Debug mode enabled");
        } else {
            System.setProperty("servermanager.logging.console.level", "INFO");
            Configurator.reconfigure();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
            getLogger().info("Debug mode disabled");
        }
    }
    
    public boolean awaitState(long timeout, TimeUnit unit) throws InterruptedException {
        if (getState().get()) {
            synchronized (getState()) {
                getState().wait(unit.toMillis(timeout));
            }
        }
        
        return getState().get();
    }
    
    public static @NonNull Server getInstance() {
        return instance;
    }
    
    public @NonNull Logger getLogger() {
        return logger;
    }
    
    public @NonNull ConfigurationImpl getConfiguration() {
        return configuration;
    }
    
    public @NonNull Optional<ConfigImpl> getConfig() {
        return Optional.ofNullable(getConfiguration().getConfig());
    }
    
    public @NonNull AtomicBoolean getState() {
        return state;
    }
    
    public @NonNull Instant getStartTime() {
        return startTime;
    }
}