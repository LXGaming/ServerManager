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

package nz.co.lolnet.servermanager.server.manager;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.configuration.ServerConfig;
import nz.co.lolnet.servermanager.server.configuration.category.ServerCategory;
import nz.co.lolnet.servermanager.server.data.Connection;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ConnectionManager {
    
    private static final Set<Connection> CONNECTIONS = Collections.synchronizedSet(Toolbox.newHashSet());
    
    public static void buildConnections() {
        List<ServerCategory> serverCategories = ServerManagerImpl.getInstance().getConfig().map(ServerConfig::getServerCategories).orElse(null);
        if (serverCategories == null || serverCategories.isEmpty()) {
            ServerManager.getInstance().getLogger().warn("Cannot build connections as Server Categories is unavailable or empty");
            return;
        }
        
        for (ServerCategory serverCategory : serverCategories) {
            if (Toolbox.isBlank(serverCategory.getName()) || serverCategory.getPlatform() == null) {
                continue;
            }
            
            String channel = Toolbox.createChannel(serverCategory.getPlatform(), serverCategory.getName());
            String name = Toolbox.createName(serverCategory.getPlatform(), serverCategory.getName());
            getConnections().add(new Connection(channel, name));
        }
    }
    
    public static void forwardState(Packet packet) {
        for (Connection connection : getConnections()) {
            if (connection.getName().equalsIgnoreCase(packet.getSender()) || !connection.isReceiveStatuses()) {
                continue;
            }
            
            // TODO Check if alive
            
            ServerManager.getInstance().sendPacket(connection.getChannel(), packet);
        }
    }
    
    public static Optional<Connection> getConnection(String name) {
        for (Connection connection : getConnections()) {
            if (Toolbox.isBlank(connection.getName())) {
                continue;
            }
            
            if (connection.getName().equalsIgnoreCase(name)) {
                return Optional.of(connection);
            }
        }
        
        return Optional.empty();
    }
    
    public static Optional<ServerCategory> getServerCategory(String name) {
        List<ServerCategory> serverCategories = ServerManagerImpl.getInstance().getConfig().map(ServerConfig::getServerCategories).orElse(null);
        if (serverCategories == null || serverCategories.isEmpty()) {
            return Optional.empty();
        }
        
        for (ServerCategory serverCategory : serverCategories) {
            if (Toolbox.isBlank(serverCategory.getName()) || serverCategory.getPlatform() == null) {
                continue;
            }
            
            if (Toolbox.createName(serverCategory.getPlatform(), serverCategory.getName()).equalsIgnoreCase(name)) {
                return Optional.of(serverCategory);
            }
        }
        
        return Optional.empty();
    }
    
    public static Set<Connection> getConnections() {
        return CONNECTIONS;
    }
}