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

package nz.co.lolnet.servermanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.util.Reference;

import java.nio.file.Path;

@Plugin(
        id = Reference.ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        description = Reference.DESCRIPTION,
        url = Reference.WEBSITE,
        authors = {Reference.AUTHORS},
        dependencies = {
                @Dependency(id = "redisvelocity")
        }
)
public class VelocityPlugin {
    
    private static VelocityPlugin instance;
    
    @Inject
    private ProxyServer proxy;
    
    @Inject
    @DataDirectory
    private Path path;
    
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        instance = this;
        ServerManagerImpl.init();
        getProxy().getEventManager().register(this, new VelocityPlugin());
        
        StatePacket packet = new StatePacket();
        packet.setState(Platform.State.SERVER_STARTED);
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        StatePacket packet = new StatePacket();
        packet.setState(Platform.State.SERVER_STOPPED);
        ServerManager.getInstance().sendResponse(packet);
        
        ServerManagerImpl.getInstance().shutdownServerManager();
    }
    
    public static String getVersion() {
        if (ProtocolVersion.SUPPORTED_VERSIONS.isEmpty()) {
            return "Unknown";
        }
        
        return ProtocolVersion.MINIMUM_VERSION.getName() + " - " + ProtocolVersion.MAXIMUM_VERSION.getName();
    }
    
    public static VelocityPlugin getInstance() {
        return instance;
    }
    
    public ProxyServer getProxy() {
        return proxy;
    }
    
    public Path getPath() {
        return path;
    }
}