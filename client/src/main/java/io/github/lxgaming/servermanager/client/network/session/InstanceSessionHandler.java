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

import com.google.common.base.Preconditions;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.client.Client;
import io.github.lxgaming.servermanager.client.entity.ConnectionImpl;
import io.github.lxgaming.servermanager.common.event.instance.ForwardEvent;
import io.github.lxgaming.servermanager.common.event.instance.ListEventImpl;
import io.github.lxgaming.servermanager.common.event.instance.MessageEventImpl;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.network.packet.ForwardPacket;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.common.network.packet.MessagePacket;

public class InstanceSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public InstanceSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        connection.setState(StateRegistry.INSTANCE);
    }
    
    @Override
    public boolean handle(DisconnectPacket packet) {
        Client.getInstance().getLogger().warn("Disconnected: {}", packet.getMessage());
        connection.close();
        return true;
    }
    
    @Override
    public boolean handle(ForwardPacket packet) {
        if (connection.getInstance().getPlatform() != Platform.SERVER) {
            connection.close();
            return true;
        }
        
        ServerManager.getInstance().getEventManager().fireAndForget(new ForwardEvent(Platform.CLIENT, connection.getInstance().getId(), packet.getInstanceIds(), packet.getDirection(), packet.getPacket()));
        return true;
    }
    
    @Override
    public boolean handle(HeartbeatPacket packet) {
        connection.write(packet);
        return true;
    }
    
    @Override
    public boolean handle(ListPacket.Response packet) {
        ServerManager.getInstance().getEventManager().fireAndForget(new ListEventImpl(Platform.CLIENT, packet.getInstance(), packet.getInstances()));
        return true;
    }
    
    @Override
    public boolean handle(MessagePacket packet) {
        Preconditions.checkState(packet.getInstanceId() != null, "InstanceId must be present");
        ServerManager.getInstance().getEventManager().fireAndForget(new MessageEventImpl(Platform.CLIENT, packet.getInstanceId(), packet.getNamespace(), packet.getPath(), packet.getValue(), packet.isPersistent()));
        return true;
    }
}