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

package io.github.lxgaming.servermanager.api;

import com.google.common.base.Preconditions;
import io.github.lxgaming.servermanager.api.event.EventManager;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ServerManager {
    
    public static final String ID = "servermanager";
    public static final String NAME = "ServerManager";
    public static final String VERSION = "@version@";
    public static final String DESCRIPTION = "Server Manager";
    public static final String AUTHORS = "LX_Gaming";
    public static final String SOURCE = "https://github.com/LXGaming/ServerManager";
    public static final String WEBSITE = "https://lxgaming.github.io/";
    
    private static ServerManager instance;
    protected EventManager eventManager;
    
    protected ServerManager() {
        ServerManager.instance = this;
    }
    
    private static <T> T check(@Nullable T instance) {
        Preconditions.checkState(instance != null, "%s has not been initialized!", ServerManager.NAME);
        return instance;
    }
    
    public static boolean isAvailable() {
        return instance != null;
    }
    
    public static ServerManager getInstance() {
        return check(instance);
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
}