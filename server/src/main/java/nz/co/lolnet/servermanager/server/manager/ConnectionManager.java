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
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
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
        List<ServerCategory> serverCategories = getServerCategories();
        if (serverCategories == null || serverCategories.isEmpty()) {
            ServerManager.getInstance().getLogger().warn("Cannot build connections as Server Categories is unavailable or empty");
            return;
        }
        
        for (ServerCategory serverCategory : serverCategories) {
            if (Toolbox.isBlank(serverCategory.getName()) || serverCategory.getPlatform() == null) {
                continue;
            }
            
            String id = Toolbox.createId(serverCategory.getPlatform(), serverCategory.getName());
            if (getConnections().add(new Connection(id, serverCategory.getName()))) {
                ServerManager.getInstance().getLogger().debug("{} Connection registered", id);
            }
        }
    }
    
    public static void forwardPacket(Packet packet) {
        List<String> clientNames = ServerManagerImpl.getInstance().getRedisService().getClientNames();
        getConnections().forEach(connection -> {
            if (connection.getSetting() == null || connection.getId().equalsIgnoreCase(packet.getSender()) || !clientNames.contains(connection.getId())) {
                return;
            }
            
            if (packet instanceof StatePacket && connection.getSetting().isForwardState()) {
                ServerManagerImpl.getInstance().sendPacket(connection.getId(), packet);
            }
        });
    }
    
    public static Optional<Connection> getConnection(String id) {
        for (Connection connection : getConnections()) {
            if (Toolbox.isBlank(connection.getId())) {
                continue;
            }
            
            if (connection.getId().equalsIgnoreCase(id)) {
                return Optional.of(connection);
            }
        }
        
        return Optional.empty();
    }
    
    public static Optional<ServerCategory> getServerCategory(String id) {
        List<ServerCategory> serverCategories = getServerCategories();
        if (serverCategories == null || serverCategories.isEmpty()) {
            return Optional.empty();
        }
        
        for (ServerCategory serverCategory : serverCategories) {
            if (Toolbox.isBlank(serverCategory.getName()) || serverCategory.getPlatform() == null) {
                continue;
            }
            
            if (Toolbox.createId(serverCategory.getPlatform(), serverCategory.getName()).equalsIgnoreCase(id)) {
                return Optional.of(serverCategory);
            }
        }
        
        return Optional.empty();
    }
    
    public static List<ServerCategory> getServerCategories() {
        return ServerManagerImpl.getInstance().getConfig()
                .map(ServerConfig::getServerCategories)
                .orElse(null);
    }
    
    public static Set<Connection> getConnections() {
        return CONNECTIONS;
    }
}