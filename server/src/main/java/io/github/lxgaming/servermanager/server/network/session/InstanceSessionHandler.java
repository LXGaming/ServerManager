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

import com.google.common.base.Preconditions;
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.event.instance.ForwardEvent;
import io.github.lxgaming.servermanager.common.event.instance.MessageEventImpl;
import io.github.lxgaming.servermanager.common.event.network.ConnectionEventImpl;
import io.github.lxgaming.servermanager.common.network.Direction;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.ForwardPacket;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.network.packet.IntentPacket;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.common.network.packet.MessagePacket;
import io.github.lxgaming.servermanager.common.util.BinaryUtils;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import io.github.lxgaming.servermanager.server.network.util.PacketUtils;

public class InstanceSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public InstanceSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        connection.setState(StateRegistry.INSTANCE);
        ServerManager.getInstance().getEventManager().fireAndForget(new ConnectionEventImpl.Instance(Platform.SERVER, connection));
    }
    
    @Override
    public boolean handle(ForwardPacket packet) {
        if (connection.getInstance().getPlatform() != Platform.SERVER) {
            connection.disconnect("Forbidden");
            return true;
        }
        
        ServerManager.getInstance().getEventManager().fireAndForget(new ForwardEvent(Platform.SERVER, connection.getInstance().getId(), packet.getInstanceIds(), packet.getDirection(), packet.getPacket()));
        return true;
    }
    
    @Override
    public boolean handle(HeartbeatPacket packet) {
        if (connection.isHeartbeatPending() && connection.getHeartbeatTime() == packet.getValue()) {
            connection.setHeartbeatPending(false);
            connection.setLatency(System.currentTimeMillis() - connection.getHeartbeatTime());
        } else {
            connection.disconnect("Timed out");
        }
        
        return true;
    }
    
    @Override
    public boolean handle(IntentPacket packet) {
        connection.setIntents(packet.getIntents());
        return true;
    }
    
    @Override
    public boolean handle(ListPacket.Request packet) {
        connection.write(PacketUtils.createListResponse());
        NetworkManager.forward(Platform.SERVER, new ForwardPacket(connection.getInstance().getId(), Direction.SERVERBOUND, packet), connection.getInstance().getId());
        return true;
    }
    
    @Override
    public boolean handle(MessagePacket packet) {
        Preconditions.checkState(packet.getInstanceId() == null, "InstanceId cannot be present");
        Instance instance = connection.getInstance();
        if (packet.isPersistent()) {
            CompoundTag compoundTag = BinaryUtils.getCompoundTag(instance.getData(), packet.getNamespace());
            BinaryUtils.mergeCompoundTags(packet.getValue(), "", compoundTag, packet.getPath());
        }
        
        packet.setInstanceId(instance.getId());
        for (Connection connection : NetworkManager.CONNECTIONS) {
            if (connection.hasIntent(packet.getNamespace() + ":" + packet.getPath())) {
                connection.write(packet);
            }
        }
        
        Server.getInstance().getLogger().debug("Received: {}", BinaryUtils.toJson(packet.getValue()));
        
        ServerManager.getInstance().getEventManager().fireAndForget(new MessageEventImpl(Platform.SERVER, instance.getId(), packet.getNamespace(), packet.getPath(), packet.getValue(), packet.isPersistent()));
        return true;
    }
}