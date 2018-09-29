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

package nz.co.lolnet.servermanager.common.manager;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.common.service.AbstractService;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServiceManager {
    
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2, Toolbox.buildThreadFactory("Service Thread #%d"));
    
    public static void schedule(AbstractService abstractService) {
        try {
            if (!abstractService.prepareService()) {
                throw new IllegalStateException("Service preparation failed");
            }
            
            schedule(abstractService, abstractService.getDelay(), abstractService.getInterval()).ifPresent(abstractService::setScheduledFuture);
        } catch (RuntimeException ex) {
            ServerManager.getInstance().getLogger().error("Encountered an error processing {}::schedule", "ServiceManager", ex);
        }
    }
    
    public static Optional<ScheduledFuture> schedule(Runnable runnable, long delay, long interval) {
        try {
            if (interval <= 0L) {
                return Optional.of(getScheduledExecutorService().schedule(runnable, Math.max(delay, 0L), TimeUnit.MILLISECONDS));
            }
            
            return Optional.of(getScheduledExecutorService().scheduleWithFixedDelay(runnable, Math.max(delay, 0L), Math.max(interval, 0L), TimeUnit.MILLISECONDS));
        } catch (RuntimeException ex) {
            ServerManager.getInstance().getLogger().error("Encountered an error processing {}::schedule", "ServiceManager", ex);
            return Optional.empty();
        }
    }
    
    public static ScheduledExecutorService getScheduledExecutorService() {
        return SCHEDULED_EXECUTOR_SERVICE;
    }
}