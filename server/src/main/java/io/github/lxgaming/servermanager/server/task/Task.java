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

package io.github.lxgaming.servermanager.server.task;

import com.google.common.base.Preconditions;
import io.github.lxgaming.servermanager.server.manager.TaskManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Task implements Runnable {
    
    private long delay;
    private long interval;
    private Type type;
    private volatile Exception exception;
    private volatile ScheduledFuture<?> scheduledFuture;
    
    public abstract boolean prepare();
    
    public abstract void execute() throws Exception;
    
    @Override
    public final void run() {
        try {
            execute();
        } catch (Exception ex) {
            TaskManager.LOGGER.error("Encountered an error while executing {}", getClass().getName(), ex);
            exception(ex);
            getScheduledFuture().cancel(false);
        }
    }
    
    public boolean await() {
        try {
            if (getScheduledFuture() == null) {
                return false;
            }
            
            getScheduledFuture().get();
            return getException() == null;
        } catch (Exception ex) {
            if (getException() == null) {
                exception(ex);
            }
            
            return false;
        }
    }
    
    public boolean await(long timeout, TimeUnit unit) {
        try {
            if (getScheduledFuture() == null) {
                return false;
            }
            
            getScheduledFuture().get(timeout, unit);
            return getException() == null;
        } catch (Exception ex) {
            if (getException() == null) {
                exception(ex);
            }
            
            return false;
        }
    }
    
    public final void schedule(ScheduledExecutorService scheduledExecutorService) throws Exception {
        Preconditions.checkNotNull(type, "type");
        
        exception(null);
        if (type == Type.DEFAULT) {
            scheduledFuture(scheduledExecutorService.schedule(this, delay, TimeUnit.MILLISECONDS));
        } else if (type == Type.FIXED_DELAY) {
            scheduledFuture(scheduledExecutorService.scheduleWithFixedDelay(this, delay, interval, TimeUnit.MILLISECONDS));
        } else if (type == Type.FIXED_RATE) {
            scheduledFuture(scheduledExecutorService.scheduleAtFixedRate(this, delay, interval, TimeUnit.MILLISECONDS));
        }
    }
    
    public final long getDelay() {
        return delay;
    }
    
    protected final void delay(long delay, TimeUnit unit) {
        this.delay = unit.toMillis(delay);
    }
    
    public final long getInterval() {
        return interval;
    }
    
    protected final void interval(long interval, TimeUnit unit) {
        this.interval = unit.toMillis(interval);
    }
    
    public final Type getType() {
        return type;
    }
    
    protected final void type(Type type) {
        this.type = type;
    }
    
    public final Exception getException() {
        return exception;
    }
    
    protected final void exception(Exception exception) {
        this.exception = exception;
    }
    
    public final ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }
    
    protected final void scheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
    
    public enum Type {
        
        DEFAULT("Default"),
        FIXED_DELAY("Fixed Delay"),
        FIXED_RATE("Fixed Rate");
        
        private final String name;
        
        Type(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}