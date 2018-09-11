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

package nz.co.lolnet.servermanager.server.util;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ForwardPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.manager.CommandManager;

public class NetworkHandlerImpl implements NetworkHandler {
    
    @Override
    public void handleCommand(CommandPacket packet) {
        if (Toolbox.isBlank(packet.getCommand())) {
            return;
        }
        
        ServerManager.getInstance().getLogger().info("Processing {} for {}", packet.getCommand(), packet.getUser());
        CommandManager.process(packet.getCommand());
    }
    
    @Override
    public void handleForward(ForwardPacket packet) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public void handleState(StatePacket packet) {
        // throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        throw new UnsupportedOperationException("Not supported");
    }
}