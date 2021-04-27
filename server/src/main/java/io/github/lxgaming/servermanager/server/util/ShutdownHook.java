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

package io.github.lxgaming.servermanager.server.util;

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.configuration.category.GeneralCategory;
import io.github.lxgaming.servermanager.common.event.lifecycle.LifecycleEventImpl;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.manager.IntegrationManager;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.TimeUnit;

public final class ShutdownHook extends Thread {
    
    @Override
    public void run() {
        Thread.currentThread().setName("Server Shutdown Thread");
        Server.getInstance().getState().set(false);
        synchronized (Server.getInstance().getState()) {
            Server.getInstance().getState().notifyAll();
        }
        
        long timeout = Server.getInstance().getConfig()
                .map(ConfigImpl::getGeneralCategory)
                .map(GeneralCategory::getShutdownTimeout)
                .orElse(GeneralCategory.DEFAULT_SHUTDOWN_TIMEOUT);
        
        NetworkManager.shutdown(timeout, TimeUnit.MILLISECONDS);
        IntegrationManager.shutdown();
        
        if (ServerManagerImpl.isAvailable()) {
            ServerManagerImpl.getInstance().getEventManager().fire(new LifecycleEventImpl.Shutdown(Platform.SERVER)).join();
            ServerManagerImpl.getInstance().shutdown(timeout, TimeUnit.MILLISECONDS);
        }
        
        LogManager.shutdown();
    }
}