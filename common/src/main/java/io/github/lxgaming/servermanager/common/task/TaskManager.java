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

package io.github.lxgaming.servermanager.common.task;

import io.github.lxgaming.servermanager.common.util.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class TaskManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);
    
    private final ScheduledExecutorService scheduledExecutorService;
    
    public TaskManager() {
        this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), Toolbox.newThreadFactory("Task Thread #%d")));
    }
    
    public TaskManager(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }
    
    public void schedule(Task task) {
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
            task.schedule(scheduledExecutorService);
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while scheduling {}", Toolbox.getClassSimpleName(task.getClass()), ex);
        }
    }
    
    public ScheduledFuture<?> schedule(Runnable runnable) {
        return scheduledExecutorService.schedule(runnable, 0L, TimeUnit.MILLISECONDS);
    }
    
    public void shutdown(long timeout, TimeUnit unit) {
        try {
            scheduledExecutorService.shutdown();
            if (!scheduledExecutorService.awaitTermination(timeout, unit)) {
                throw new InterruptedException();
            }
            
            LOGGER.info("Successfully terminated task, continuing with shutdown process...");
        } catch (Exception ex) {
            LOGGER.error("Failed to terminate task, continuing with shutdown process...");
        }
    }
}