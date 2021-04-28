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

package io.github.lxgaming.servermanager.server.listener;

import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.event.instance.ForwardEvent;
import io.github.lxgaming.servermanager.common.network.Direction;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.common.util.StringUtils;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import io.github.lxgaming.servermanager.server.network.util.PacketUtils;
import net.kyori.event.method.annotation.Subscribe;

import java.util.UUID;

public class InstanceListener {
    
    @Subscribe
    public void onForward(ForwardEvent event) {
        if (event.getDirection() == Direction.CLIENTBOUND) {
            if (event.getInstanceIds().isEmpty()) {
                return;
            }
            
            UUID instanceId = event.getInstanceIds().remove(event.getInstanceIds().size() - 1);
            if (event.getInstanceIds().isEmpty()) {
                NetworkManager.write(instanceId, event.getPacket());
            } else {
                NetworkManager.forward(instanceId, event.getDirection(), event.getPacket(), event.getInstanceIds());
            }
            
            return;
        }
        
        if (event.getDirection() == Direction.SERVERBOUND) {
            if (event.getInstanceIds().contains(event.getInstanceId())) {
                ServerManagerImpl.getInstance().getLogger().warn("Route Loop Detected! ({})", StringUtils.join(event.getInstanceIds(), " -> "));
                return;
            }
            
            if (event.getPacket() instanceof ListPacket.Request) {
                NetworkManager.forward(event.getInstanceId(), Direction.CLIENTBOUND, PacketUtils.createListResponse(), event.getInstanceIds());
            }
            
            event.getInstanceIds().add(event.getInstanceId());
            NetworkManager.forward(event.getDirection(), event.getPacket(), event.getInstanceIds());
        }
    }
}