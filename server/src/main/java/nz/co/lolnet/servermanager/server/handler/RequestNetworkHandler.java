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

package nz.co.lolnet.servermanager.server.handler;

import nz.co.lolnet.servermanager.api.network.AbstractNetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.ListPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.data.Connection;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

import java.util.stream.Collectors;

public class RequestNetworkHandler extends AbstractNetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        if (!packet.getType().equals(Packet.Type.REQUEST)) {
            return false;
        }
        
        if (Toolbox.isNotBlank(packet.getForwardTo())) {
            ConnectionManager.getConnection(packet.getForwardTo()).ifPresent(connection -> {
                ServerManagerImpl.getInstance().sendPacket(connection.getChannel(), packet);
            });
            
            return false;
        }
        
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> connection.setLastPacketTime(System.currentTimeMillis()));
        ConnectionManager.forwardPacket(packet);
        return true;
    }
    
    @Override
    public void handleList(ListPacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            packet.setServers(ConnectionManager.getConnections().stream().map(Connection::getName).collect(Collectors.toSet()));
            ServerManagerImpl.getInstance().sendResponse(connection.getChannel(), packet);
        });
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            ServerManagerImpl.getInstance().sendResponse(connection.getChannel(), packet);
        });
    }
}