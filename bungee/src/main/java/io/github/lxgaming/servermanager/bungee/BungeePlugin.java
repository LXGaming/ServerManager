/*
 * Copyright 2018 Alex Thomson
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

package io.github.lxgaming.servermanager.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.ProtocolConstants;
import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.network.packet.StatePacket;
import io.github.lxgaming.servermanager.bungee.listener.BungeeListener;

public class BungeePlugin extends Plugin implements Platform {
    
    private static BungeePlugin instance;
    
    @Override
    public void onLoad() {
        instance = this;
    }
    
    @Override
    public void onEnable() {
        ServerManagerImpl.init();
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeListener());
        
        StatePacket packet = new StatePacket();
        packet.setState(Platform.State.SERVER_STARTED);
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void onDisable() {
        StatePacket packet = new StatePacket();
        packet.setState(Platform.State.SERVER_STOPPED);
        ServerManager.getInstance().sendResponse(packet);
        
        ServerManagerImpl.getInstance().shutdownServerManager();
    }
    
    public static String getVersion() {
        if (ProtocolConstants.SUPPORTED_VERSIONS.isEmpty()) {
            return "Unknown";
        }
        
        return ProtocolConstants.SUPPORTED_VERSIONS.get(0) + " - " + ProtocolConstants.SUPPORTED_VERSIONS.get(ProtocolConstants.SUPPORTED_VERSIONS.size() - 1);
    }
    
    public static BungeePlugin getInstance() {
        return instance;
    }
}