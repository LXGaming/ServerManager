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

package io.github.lxgaming.servermanager.server.handler;

import io.github.lxgaming.servermanager.api.network.AbstractNetworkHandler;
import io.github.lxgaming.servermanager.api.network.Packet;
import io.github.lxgaming.servermanager.api.network.packet.ListPacket;
import io.github.lxgaming.servermanager.api.network.packet.PingPacket;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.manager.ConnectionManager;

public class RequestNetworkHandler extends AbstractNetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        if (!packet.getType().equals(Packet.Type.REQUEST)) {
            return false;
        }
        
        if (Toolbox.isNotBlank(packet.getForwardTo())) {
            ConnectionManager.getConnection(packet.getForwardTo()).ifPresent(connection -> {
                ServerManagerImpl.getInstance().sendPacket(connection.getId(), packet);
            });
            
            return false;
        }
        
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> connection.setLastPacketTime(System.currentTimeMillis()));
        return true;
    }
    
    @Override
    public void handleListBasic(ListPacket.Basic packet) {
        packet.setImplementations(Toolbox.newHashSet());
        ConnectionManager.getConnections().forEach(connection -> {
            if (connection.getId().equalsIgnoreCase(packet.getSender()) || connection.getType().isUnknown()) {
                return;
            }
            
            packet.getImplementations().add(connection);
        });
        
        ServerManagerImpl.getInstance().sendResponse(packet.getSender(), packet);
    }
    
    @Override
    public void handleListFull(ListPacket.Full packet) {
        packet.setImplementations(Toolbox.newHashMap());
        ConnectionManager.getConnections().forEach(connection -> {
            if (connection.getId().equalsIgnoreCase(packet.getSender()) || connection.getType().isUnknown()) {
                return;
            }
            
            packet.getImplementations().put(connection, connection.getData());
        });
        
        ServerManagerImpl.getInstance().sendResponse(packet.getSender(), packet);
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            ServerManagerImpl.getInstance().sendResponse(connection.getId(), packet);
        });
    }
}