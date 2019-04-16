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

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.AbstractNetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.SettingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.api.network.packet.UserPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.manager.CommandManager;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

public class ResponseNetworkHandler extends AbstractNetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        if (!packet.getType().equals(Packet.Type.RESPONSE)) {
            return false;
        }
        
        if (Toolbox.isNotBlank(packet.getForwardTo())) {
            ConnectionManager.getConnection(packet.getForwardTo()).ifPresent(connection -> {
                ServerManagerImpl.getInstance().sendPacket(connection.getId(), packet);
            });
            
            return false;
        }
        
        ConnectionManager.forwardPacket(packet);
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> connection.setLastPacketTime(System.currentTimeMillis()));
        return true;
    }
    
    @Override
    public void handleCommand(CommandPacket packet) {
        if (Toolbox.isBlank(packet.getCommand())) {
            return;
        }
        
        ServerManager.getInstance().getLogger().info("Processing {} for {}", packet.getCommand(), packet.getUser());
        CommandManager.process(packet.getCommand());
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        ServerManager.getInstance().getLogger().info("{}ms Ping from {}", System.currentTimeMillis() - packet.getTime(), packet.getSender());
    }
    
    @Override
    public void handleSetting(SettingPacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            if (packet.getSetting() == null) {
                return;
            }
            
            connection.setSetting(packet.getSetting());
            ServerManager.getInstance().getLogger().info("Received Setting from {}", packet.getSender());
        });
    }
    
    @Override
    public void handleState(StatePacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            if (packet.getState() == null) {
                return;
            }
            
            if (connection.getData().getState() != Platform.State.FROZEN && packet.getState() == Platform.State.SERVER_STARTED) {
                connection.setSetting(null);
                connection.getData().setStartTime(null);
            }
            
            connection.getData().setState(packet.getState());
            ServerManager.getInstance().getLogger().info("{} State from {}", packet.getState().getName(), packet.getSender());
        });
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            connection.getData().setLastTickTime(packet.getData().getLastTickTime());
            if (!connection.getData().getStartTime().equals(packet.getData().getStartTime())) {
                connection.setSetting(null);
                connection.getData().setStartTime(packet.getData().getStartTime());
            }
            
            connection.getData().setState(packet.getData().getState());
            connection.getData().setTicksPerSecond(packet.getData().getTicksPerSecond());
            connection.getData().setUsers(packet.getData().getUsers());
            connection.getData().setVersion(packet.getData().getVersion());
            ServerManager.getInstance().getLogger().info("Received Status from {}", packet.getSender());
        });
    }
    
    @Override
    public void handleUserAdd(UserPacket.Add packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            if (packet.getUser() == null) {
                return;
            }
            
            if (connection.getData().getUsers() == null) {
                connection.getData().setUsers(Toolbox.newHashSet());
            }
            
            connection.getData().getUsers().add(packet.getUser());
            ServerManager.getInstance().getLogger().info("Received User.Add from {}", packet.getSender());
        });
    }
    
    @Override
    public void handleUserRemove(UserPacket.Remove packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            if (packet.getUser() == null || connection.getData().getUsers() == null) {
                return;
            }
            
            connection.getData().getUsers().remove(packet.getUser());
            ServerManager.getInstance().getLogger().info("Received User.Remove from {}", packet.getSender());
        });
    }
}