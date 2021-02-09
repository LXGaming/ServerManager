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

import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.network.packet.HandshakePacket;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;
import io.netty.buffer.ByteBuf;

public class HandshakeSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public HandshakeSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        ServerManagerImpl.getInstance().getLogger().info("HandshakeSessionHandler active");
        connection.setState(StateRegistry.HANDSHAKE);
    }
    
    @Override
    public boolean handle(HandshakePacket packet) {
        if (packet.getState() == StateRegistry.STATUS) {
            connection.setSessionHandler(new StatusSessionHandler(connection));
            return true;
        }
        
        if (packet.getState() == StateRegistry.LOGIN) {
            connection.setSessionHandler(new LoginSessionHandler(connection));
            return true;
        }
        
        connection.closeWith(new DisconnectPacket("Unsupported State"));
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