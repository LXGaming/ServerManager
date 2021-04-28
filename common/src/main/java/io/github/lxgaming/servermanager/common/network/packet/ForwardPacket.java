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

package io.github.lxgaming.servermanager.common.network.packet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.lxgaming.servermanager.common.network.Direction;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.UUID;

public class ForwardPacket implements Packet {
    
    public static final int ARRAY_LENGTH = 16;
    
    private List<UUID> instanceIds;
    private Direction direction;
    private Packet packet;
    
    public ForwardPacket() {
    }
    
    public ForwardPacket(List<UUID> instanceIds, Direction direction, Packet packet) {
        this.instanceIds = instanceIds;
        this.direction = direction;
        this.packet = packet;
    }
    
    @Override
    public void decode(ByteBuf byteBuf) {
        UUID[] array = ProtocolUtils.readUUIDArray(byteBuf, ARRAY_LENGTH);
        int directionId = ProtocolUtils.readVarInt(byteBuf);
        int packetId = ProtocolUtils.readVarInt(byteBuf);
        this.instanceIds = Lists.newArrayList(array);
        this.direction = Direction.getDirection(directionId);
        this.packet = StateRegistry.INSTANCE.createPacket(direction, packetId);
        packet.decode(byteBuf);
    }
    
    @Override
    public void encode(ByteBuf byteBuf) {
        Preconditions.checkNotNull(instanceIds, "instanceIds");
        Preconditions.checkNotNull(direction, "direction");
        Preconditions.checkNotNull(packet, "packet");
        Preconditions.checkState(instanceIds.size() <= ARRAY_LENGTH, "InstanceIds exceeds maximum length");
        Preconditions.checkState(!(packet instanceof ForwardPacket), "Packet cannot be a ForwardPacket");
        UUID[] array = instanceIds.toArray(new UUID[0]);
        int packetId = StateRegistry.INSTANCE.getPacketId(direction, packet.getClass());
        ProtocolUtils.writeUUIDArray(byteBuf, array);
        ProtocolUtils.writeVarInt(byteBuf, direction.getId());
        ProtocolUtils.writeVarInt(byteBuf, packetId);
        packet.encode(byteBuf);
    }
    
    @Override
    public boolean handle(SessionHandler handler) {
        return handler.handle(this);
    }
    
    public List<UUID> getInstanceIds() {
        return instanceIds;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public Packet getPacket() {
        return packet;
    }
}