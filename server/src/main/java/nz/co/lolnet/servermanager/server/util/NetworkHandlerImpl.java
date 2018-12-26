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

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.ServerInfo;
import nz.co.lolnet.servermanager.api.network.AbstractNetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ListPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.SettingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.data.Connection;
import nz.co.lolnet.servermanager.server.manager.CommandManager;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

import java.util.stream.Collectors;

public class NetworkHandlerImpl extends AbstractNetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        ServerManager.getInstance().getLogger().info("Received Packet from {} ({})", packet.getSender(), packet.getType());
        if (Toolbox.isNotBlank(packet.getForwardTo())) {
            ConnectionManager.getConnection(packet.getForwardTo()).ifPresent(connection -> {
                ServerManagerImpl.getInstance().sendPacket(connection.getChannel(), packet);
            });
            
            return false;
        }
        
        if (packet.getType().equals(Packet.Type.RESPONSE)) {
            ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> connection.setLastPacketTime(System.currentTimeMillis()));
            ConnectionManager.forwardPacket(packet);
            return true;
        }
        
        return false;
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
    public void handleList(ListPacket packet) {
        ConnectionManager.getConnection(packet.getSender()).ifPresent(connection -> {
            packet.setServers(ConnectionManager.getConnections().stream().map(Connection::getName).collect(Collectors.toSet()));
            ServerManagerImpl.getInstance().sendResponse(connection.getChannel(), packet);
        });
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        ServerManager.getInstance().getLogger().info("{}ms Ping from {}", System.currentTimeMillis() - packet.getTime(), packet.getSender());
    }
    
    @Override
    public void handleSetting(SettingPacket packet) {
        Connection connection = ConnectionManager.getConnection(packet.getSender()).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().warn("Received {} from unregistered server {}", packet.getClass().getSimpleName(), packet.getSender());
            return;
        }
        
        connection.setSetting(packet.getSetting());
        ServerManager.getInstance().getLogger().info("Received Setting from {}", packet.getSender());
    }
    
    @Override
    public void handleState(StatePacket packet) {
        Connection connection = ConnectionManager.getConnection(packet.getSender()).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().warn("Received {} from unregistered server {}", packet.getClass().getSimpleName(), packet.getSender());
            return;
        }
        
        if (connection.getServerInfo() == null) {
            connection.setServerInfo(new ServerInfo());
        }
        
        connection.getServerInfo().setState(packet.getState());
        if (connection.getServerInfo().getState().equals(Platform.State.SERVER_STARTED)) {
            ServerManagerImpl.getInstance().sendRequest(connection.getChannel(), new SettingPacket());
        }
        
        ServerManager.getInstance().getLogger().info("{} State from {}", packet.getState().getFriendlyName(), packet.getSender());
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        Connection connection = ConnectionManager.getConnection(packet.getSender()).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().warn("Received {} from unregistered server {}", packet.getClass().getSimpleName(), packet.getSender());
            return;
        }
        
        connection.setServerInfo(packet.getServerInfo());
        ServerManager.getInstance().getLogger().info("{} Status from {}", packet.getServerInfo().toString(), packet.getSender());
    }
}