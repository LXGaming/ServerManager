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
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.client.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.client.configuration.ConfigurationImpl;
import io.github.lxgaming.servermanager.client.manager.NetworkManager;
import io.github.lxgaming.servermanager.client.util.ShutdownHook;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.event.lifecycle.LifecycleEventImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Client {
    
    private static Client instance;
    private final Logger logger;
    private final ConfigurationImpl configuration;
    private final AtomicBoolean state;
    private Connection connection;
    
    public Client(@NonNull Path path) {
        instance = this;
        this.logger = LoggerFactory.getLogger(Client.class);
        this.configuration = new ConfigurationImpl(path);
        this.state = new AtomicBoolean(false);
    }
    
    public boolean prepare() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        if (!getConfiguration().loadConfiguration()) {
            getLogger().error("Failed to load");
            return false;
        }
        
        NetworkManager.prepare();
        
        ServerManager.getInstance().getEventManager().fire(new LifecycleEventImpl.Initialize(Platform.CLIENT)).join();
        
        getConfiguration().saveConfiguration();
        getState().set(true);
        return true;
    }
    
    public void execute() {
        NetworkManager.execute();
    }
    
    public static @NonNull Client getInstance() {
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
    
    public @Nullable Connection getConnection() {
        return connection;
    }
    
    public void setConnection(@Nullable Connection connection) {
        this.connection = connection;
    }
}