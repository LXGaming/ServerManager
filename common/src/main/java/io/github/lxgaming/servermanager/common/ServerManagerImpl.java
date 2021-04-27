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

package io.github.lxgaming.servermanager.common;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.event.EventManager;
import io.github.lxgaming.servermanager.common.event.EventManagerImpl;
import io.github.lxgaming.servermanager.common.task.TaskManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public final class ServerManagerImpl extends ServerManager {
    
    private final EventManager eventManager;
    private final Logger logger;
    private final TaskManager taskManager;
    
    private ServerManagerImpl() {
        super();
        this.eventManager = new EventManagerImpl();
        this.logger = LoggerFactory.getLogger(ServerManager.class);
        this.taskManager = new TaskManager();
    }
    
    public static synchronized void init() {
        if (ServerManager.isAvailable()) {
            return;
        }
        
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger().info("{} v{} initialized", ServerManager.NAME, ServerManager.VERSION);
    }
    
    public void shutdown(long timeout, @NonNull TimeUnit unit) {
        getLogger().info("Shutting down...");
        getEventManager().shutdown(timeout, unit);
        getTaskManager().shutdown(timeout, unit);
        super.shutdown();
    }
    
    public static @NonNull ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    @Override
    public @NonNull EventManagerImpl getEventManager() {
        return (EventManagerImpl) eventManager;
    }
    
    public @NonNull Logger getLogger() {
        return logger;
    }
    
    public @NonNull TaskManager getTaskManager() {
        return taskManager;
    }
}