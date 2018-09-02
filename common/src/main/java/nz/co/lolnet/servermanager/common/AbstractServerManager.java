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

package nz.co.lolnet.servermanager.common;

import nz.co.lolnet.servermanager.common.configuration.IConfiguration;
import nz.co.lolnet.servermanager.common.managers.PacketManager;
import nz.co.lolnet.servermanager.common.network.INetworkHandler;
import nz.co.lolnet.servermanager.common.util.Logger;
import nz.co.lolnet.servermanager.common.util.Reference;

import java.nio.file.Path;

public abstract class AbstractServerManager {
    
    private static AbstractServerManager instance;
    protected Logger logger;
    protected Path path;
    protected IConfiguration configuration;
    protected INetworkHandler networkHandler;
    
    protected AbstractServerManager() {
        instance = this;
    }
    
    public void loadServerManager() {
        getLogger().info("Initializing...");
        getConfiguration().loadConfiguration();
        PacketManager.buildPackets();
        getConfiguration().saveConfiguration();
        getLogger().info("{} v{} has loaded", Reference.NAME, Reference.VERSION);
    }
    
    public static AbstractServerManager getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Path getPath() {
        return path;
    }
    
    public IConfiguration getConfiguration() {
        return configuration;
    }
    
    public INetworkHandler getNetworkHandler() {
        return networkHandler;
    }
}