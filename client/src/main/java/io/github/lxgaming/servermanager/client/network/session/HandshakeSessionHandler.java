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

package io.github.lxgaming.servermanager.client.network.session;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.client.Client;
import io.github.lxgaming.servermanager.client.entity.ConnectionImpl;
import io.github.lxgaming.servermanager.common.event.network.ConnectionEventImpl;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.network.packet.HandshakePacket;

public class HandshakeSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public HandshakeSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        connection.setState(StateRegistry.HANDSHAKE);
        ServerManager.getInstance().getEventManager().fireAndForget(new ConnectionEventImpl.Handshake(Platform.CLIENT, connection));
    }
    
    @Override
    public void connected() {
        Client.getInstance().getLogger().info("Connection established");
        
        connection.write(new HandshakePacket(StateRegistry.LOGIN));
        connection.setSessionHandler(new LoginSessionHandler(connection));
    }
    
    @Override
    public boolean handle(DisconnectPacket packet) {
        Client.getInstance().getLogger().warn("Disconnected: {}", packet.getMessage());
        connection.close();
        return true;
    }
}