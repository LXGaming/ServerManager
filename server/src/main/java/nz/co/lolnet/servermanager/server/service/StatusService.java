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

package nz.co.lolnet.servermanager.server.service;

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.Setting;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.SettingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.common.service.AbstractService;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.configuration.ServerConfig;
import nz.co.lolnet.servermanager.server.configuration.category.ServerCategory;
import nz.co.lolnet.servermanager.server.data.Connection;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class StatusService extends AbstractService {
    
    private final Set<Connection> invalidConnections = Toolbox.newHashSet();
    
    @Override
    public boolean prepareService() {
        setInterval(5000L);
        return true;
    }
    
    @Override
    public void executeService() {
        List<ServerCategory> serverCategories = ConnectionManager.getServerCategories();
        if (serverCategories == null || serverCategories.isEmpty()) {
            return;
        }
        
        List<String> clientNames = ServerManagerImpl.getInstance().getRedisService().getClientNames();
        ConnectionManager.getConnections().forEach(connection -> {
            if (connection.getType().isUnknown()) {
                return;
            }
            
            if (clientNames.contains(connection.getId())) {
                handleConnected(connection);
            } else {
                handleDisconnected(connection);
            }
        });
        
        ConnectionManager.getConnections().removeAll(invalidConnections);
        invalidConnections.clear();
    }
    
    private void handleConnected(Connection connection) {
        if (connection.getData().getState() == null || connection.getData().getState().isUnknown() || connection.getData().getState() == Platform.State.DISCONNECTED) {
            updateState(connection, Platform.State.CONNECTED);
        }
        
        if (connection.getData().getState().isOnline() && connection.getData().getStartTime() == null) {
            connection.getData().setStartTime(0L);
            ServerManagerImpl.getInstance().sendRequest(connection.getId(), new StatusPacket());
        }
        
        if (connection.getData().getState() == Platform.State.SERVER_STARTED && connection.getSetting() == null) {
            Setting setting = new Setting();
            setting.setTickTime(30000L);
            connection.setSetting(setting);
            
            SettingPacket packet = new SettingPacket();
            packet.setSetting(connection.getSetting());
            ServerManagerImpl.getInstance().sendRequest(connection.getId(), packet);
        }
    }
    
    private void handleDisconnected(Connection connection) {
        ServerCategory serverCategory = ConnectionManager.getServerCategory(connection.getId()).orElse(null);
        if (serverCategory == null) {
            ServerManager.getInstance().getLogger().info("Removing {}...", connection.getId());
            invalidConnections.add(connection);
            return;
        }
        
        if (connection.getData().getState() == null || connection.getData().getState().isUnknown() || connection.getData().getState() == Platform.State.CONNECTED) {
            updateState(connection, Platform.State.DISCONNECTED);
        }
        
        if (connection.getData().getState() == Platform.State.JVM_STARTED) {
            if (connection.getData().getStartTime() != null && (System.currentTimeMillis() - connection.getData().getStartTime()) >= 30000L) {
                ServerManager.getInstance().getLogger().warn("{} has failed to start", connection.getName());
                updateState(connection, Platform.State.DISCONNECTED);
                return;
            }
            
            return;
        }
        
        if (!connection.getData().getState().isOffline()) {
            if ((System.currentTimeMillis() - connection.getLastPacketTime()) >= 30000L) {
                ServerManager.getInstance().getLogger().warn("{} didn't shutdown correctly?", connection.getName());
                updateState(connection, Platform.State.DISCONNECTED);
            }
        }
        
        if (connection.getData().getState() == Platform.State.JVM_STOPPED) {
            if (Toolbox.isBlank(serverCategory.getPath()) || !serverCategory.isAutoRestart()) {
                return;
            }
            
            ServerManager.getInstance().getLogger().info("Starting {}...", connection.getName());
            if (startServer(serverCategory.getPath(), serverCategory.getScript())) {
                connection.getData().setStartTime(System.currentTimeMillis());
                updateState(connection, Platform.State.JVM_STARTED);
            } else {
                updateState(connection, Platform.State.DISCONNECTED);
            }
        }
    }
    
    private void updateState(Connection connection, Platform.State state) {
        if (connection.getData().getState() == state) {
            return;
        }
        
        connection.getData().setState(state);
        StatePacket packet = new StatePacket();
        packet.setSender(connection.getId());
        packet.setState(connection.getData().getState());
        packet.setType(Packet.Type.RESPONSE);
        ConnectionManager.forwardPacket(packet);
    }
    
    private boolean startServer(String path, String script) {
        Path basePath = Paths.get(path);
        if (Files.notExists(basePath) || !Files.isDirectory(basePath)) {
            ServerManager.getInstance().getLogger().warn("Failed to start server: Provided path does not point to an existing directory");
            return false;
        }
        
        Path scriptPath = basePath.resolve(script);
        if (Files.notExists(scriptPath) || !Files.isRegularFile(scriptPath)) {
            ServerManager.getInstance().getLogger().warn("Failed to start server: Provided script doesn't exist");
            return false;
        }
        
        String command = ServerManagerImpl.getInstance().getConfig().map(ServerConfig::getCommand).orElse(null);
        if (Toolbox.isBlank(command)) {
            ServerManager.getInstance().getLogger().warn("Failed to start server: Command is blank");
            return false;
        }
        
        command = command
                .replace("[PATH]", basePath.toString())
                .replace("[SCRIPT]", script);
        
        try {
            Runtime.getRuntime().exec(command, null, basePath.toFile());
            return true;
        } catch (IllegalArgumentException | IOException | NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}