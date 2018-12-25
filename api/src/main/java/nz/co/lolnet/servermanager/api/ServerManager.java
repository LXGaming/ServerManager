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

package nz.co.lolnet.servermanager.api;

import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.util.Logger;

import java.util.Optional;

public abstract class ServerManager {
    
    private static ServerManager instance;
    protected Platform.Type platformType;
    protected Logger logger;
    
    protected ServerManager() {
        instance = this;
    }
    
    protected abstract void loadServerManager();
    
    protected abstract void reloadServerManager();
    
    public abstract boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass);
    
    public void sendRequest(Packet packet) {
        packet.setSender(null);
        packet.setType(Packet.Type.REQUEST);
        sendPacket(packet);
    }
    
    public void sendResponse(Packet packet) {
        packet.setSender(null);
        packet.setType(Packet.Type.RESPONSE);
        sendPacket(packet);
    }
    
    protected void sendPacket(Packet packet) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    public abstract Optional<? extends Config> getConfig();
    
    public static ServerManager getInstance() {
        return instance;
    }
    
    public final Platform.Type getPlatformType() {
        return platformType;
    }
    
    public final Logger getLogger() {
        return logger;
    }
}