/*
 * Copyright 2021 Alex Thomson
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

package io.github.lxgaming.servermanager.server.network.session;

import io.github.lxgaming.servermanager.common.manager.InstanceManager;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.HelloPacket;
import io.github.lxgaming.servermanager.common.network.packet.LoginPacket;
import io.github.lxgaming.servermanager.common.util.StringUtils;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.configuration.category.InstanceCategory;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;
import io.netty.buffer.ByteBuf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

public class LoginSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public LoginSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        ServerManagerImpl.getInstance().getLogger().info("LoginSessionHandler active");
        connection.setState(StateRegistry.LOGIN);
        
        if (Toolbox.isPrivateAddress(connection.getAddress())) {
            connection.write(new HelloPacket(0, false));
        } else {
            // TODO Add support for Compression and Encryption.
            // connection.write(new HelloPacket(256, true));
            connection.disconnect("Remote connections are not currently supported");
        }
    }
    
    @Override
    public boolean handle(LoginPacket.Request packet) {
        if (StringUtils.isBlank(packet.getPath())) {
            connection.write(new LoginPacket.Response());
            connection.setSessionHandler(new ApplicationSessionHandler(connection));
            return true;
        }
        
        if (!Toolbox.isPrivateAddress(connection.getAddress())) {
            connection.disconnect("Forbidden");
            return true;
        }
        
        Path path = Paths.get(packet.getPath());
        if (!Files.exists(path)) {
            connection.disconnect("Path does not exist");
            return true;
        }
        
        if (!Files.isDirectory(path)) {
            connection.disconnect("Path does not point to a directory");
            return true;
        }
        
        Set<InstanceCategory> instanceCategories = ServerManagerImpl.getInstance().getConfig().map(ConfigImpl::getInstanceCategories).orElse(null);
        if (instanceCategories == null) {
            connection.disconnect("Registration is unavailable");
            return true;
        }
        
        InstanceCategory instanceCategory = instanceCategories.stream()
                .filter(category -> category.getPath().equals(path.toString()))
                .findFirst()
                .orElseGet(() -> {
                    InstanceCategory category = new InstanceCategory(UUID.randomUUID(), packet.getName(), path.toString());
                    if (instanceCategories.add(category)) {
                        ServerManagerImpl.getInstance().getConfiguration().saveConfiguration();
                        return category;
                    }
                    
                    return null;
                });
        
        if (instanceCategory == null) {
            connection.disconnect("Registration failed");
            return true;
        }
        
        ServerManagerImpl.getInstance().getLogger().info("{} ({}) logged in", instanceCategory.getName(), instanceCategory.getId());
        
        connection.setAssociation(InstanceManager.getOrCreateInstance(instanceCategory.getId(), instanceCategory.getName()));
        connection.write(new LoginPacket.Response(instanceCategory.getId()));
        connection.setSessionHandler(new InstanceSessionHandler(connection));
        return true;
    }
    
    @Override
    public void handleGeneric(Packet packet) {
        connection.close();
    }
    
    @Override
    public void handleUnknown(ByteBuf byteBuf) {
        connection.close();
    }
}