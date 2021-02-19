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
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.client.ServerManagerImpl;
import io.github.lxgaming.servermanager.client.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.client.configuration.category.GeneralCategoryImpl;
import io.github.lxgaming.servermanager.client.entity.ConnectionImpl;
import io.github.lxgaming.servermanager.common.event.connection.MessageEventImpl;
import io.github.lxgaming.servermanager.common.manager.InstanceManager;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.common.network.packet.MessagePacket;
import io.github.lxgaming.servermanager.common.util.BinaryUtils;

import java.util.UUID;

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
        ServerManagerImpl.getInstance().getLogger().warn("Disconnected: {}", packet.getMessage());
        connection.close();
        return true;
    }
    
    @Override
    public boolean handle(HeartbeatPacket packet) {
        connection.write(packet);
        return true;
    }
    
    @Override
    public boolean handle(ListPacket.Response packet) {
        UUID id = ServerManagerImpl.getInstance().getConfig().map(ConfigImpl::getGeneralCategory).map(GeneralCategoryImpl::getId).orElse(null);
        InstanceManager.INSTANCES.removeIf(instance -> !instance.getId().equals(id));
        InstanceManager.INSTANCES.addAll(packet.getInstances());
        return true;
    }
    
    @Override
    public boolean handle(MessagePacket packet) {
        Preconditions.checkState(packet.getOrigin() != null, "Origin must be present");
        Instance instance = InstanceManager.getOrCreateInstance(packet.getOrigin(), "Unknown");
        if (instance == null) {
            return true;
        }
        
        if (packet.isPersistent()) {
            CompoundTag compoundTag = BinaryUtils.getCompoundTag(instance.getData(), packet.getKey());
            BinaryUtils.mergeCompoundTags(compoundTag, packet.getValue());
        }
        
        ServerManager.getInstance().getEventManager().fireAndForget(new MessageEventImpl(instance, packet.getKey(), packet.getValue(), packet.isPersistent()));
        return true;
    }
}