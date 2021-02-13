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

package io.github.lxgaming.servermanager.server.manager;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.configuration.category.TaskCategory;
import io.github.lxgaming.servermanager.server.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TaskManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.NAME);
    public static final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(0, Toolbox.newThreadFactory("Task Thread #%d"));
    
    public static void prepare() {
        TaskCategory taskCategory = ServerManagerImpl.getInstance().getConfig().map(ConfigImpl::getTaskCategory).orElseThrow(NullPointerException::new);
        if (taskCategory.getCorePoolSize() < 0) {
            LOGGER.warn("CorePoolSize is out of bounds. Resetting to {}", TaskCategory.DEFAULT_CORE_POOL_SIZE);
            taskCategory.setCorePoolSize(TaskCategory.DEFAULT_CORE_POOL_SIZE);
        }
        
        if (taskCategory.getShutdownTimeout() < 0) {
            LOGGER.warn("ShutdownTimeout is out of bounds. Resetting to {}", TaskCategory.DEFAULT_SHUTDOWN_TIMEOUT);
            taskCategory.setShutdownTimeout(TaskCategory.DEFAULT_SHUTDOWN_TIMEOUT);
        }
        
        SCHEDULED_EXECUTOR_SERVICE.setCorePoolSize(taskCategory.getCorePoolSize());
    }
    
    public static void schedule(Task task) {
        try {
            if (!task.prepare()) {
                LOGGER.warn("{} failed to prepare", Toolbox.getClassSimpleName(task.getClass()));
                return;
            }
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while preparing {}", Toolbox.getClassSimpleName(task.getClass()), ex);
            return;
        }
        
        try {
            task.schedule(SCHEDULED_EXECUTOR_SERVICE);
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while scheduling {}", Toolbox.getClassSimpleName(task.getClass()), ex);
        }
    }
    
    public static ScheduledFuture<?> schedule(Runnable runnable) {
        return SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, 0L, TimeUnit.MILLISECONDS);
    }
    
    public static void shutdown() {
        try {
            long timeout = ServerManagerImpl.getInstance().getConfig()
                    .map(ConfigImpl::getTaskCategory)
                    .map(TaskCategory::getShutdownTimeout)
                    .orElse(TaskCategory.DEFAULT_SHUTDOWN_TIMEOUT);
            
            SCHEDULED_EXECUTOR_SERVICE.shutdown();
            if (!SCHEDULED_EXECUTOR_SERVICE.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                throw new InterruptedException();
            }
            
            LOGGER.info("Successfully terminated task, continuing with shutdown process...");
        } catch (Exception ex) {
            LOGGER.error("Failed to terminate task, continuing with shutdown process...");
        }
    }
}