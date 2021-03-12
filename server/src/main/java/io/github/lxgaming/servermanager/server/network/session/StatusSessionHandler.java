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

import com.google.gson.JsonObject;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.event.network.ConnectionEventImpl;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.StatusPacket;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;
import io.netty.buffer.ByteBuf;

public class StatusSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public StatusSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        connection.setState(StateRegistry.STATUS);
        ServerManager.getInstance().getEventManager().fireAndForget(new ConnectionEventImpl.Status(Platform.SERVER, connection));
        
        JsonObject status = new JsonObject();
        status.addProperty("name", ServerManager.NAME);
        status.addProperty("version", ServerManager.VERSION);
        connection.closeWith(new StatusPacket(status));
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