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
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class MessagePacket implements Packet {
    
    public static final int KEY_LENGTH = 255;
    
    private String key;
    private CompoundTag value;
    private boolean persistent;
    private UUID origin;
    
    public MessagePacket() {
    }
    
    public MessagePacket(String key, CompoundTag value) {
        this(key, value, false);
    }
    
    public MessagePacket(String key, CompoundTag value, boolean persistent) {
        this(key, value, persistent, null);
    }
    
    public MessagePacket(String key, CompoundTag value, boolean persistent, UUID origin) {
        this.key = key;
        this.value = value;
        this.persistent = persistent;
        this.origin = origin;
    }
    
    @Override
    public void decode(ByteBuf byteBuf) {
        this.key = ProtocolUtils.readString(byteBuf, KEY_LENGTH);
        this.value = (CompoundTag) ProtocolUtils.readTag(byteBuf);
        this.persistent = byteBuf.readBoolean();
        if (byteBuf.readBoolean()) {
            this.origin = ProtocolUtils.readUUID(byteBuf);
        }
    }
    
    @Override
    public void encode(ByteBuf byteBuf) {
        Preconditions.checkNotNull(key, "key");
        Preconditions.checkNotNull(value, "value");
        Preconditions.checkState(key.length() <= KEY_LENGTH, "Key exceeds maximum length");
        ProtocolUtils.writeString(byteBuf, key);
        ProtocolUtils.writeTag(byteBuf, value);
        byteBuf.writeBoolean(persistent);
        byteBuf.writeBoolean(origin != null);
        if (origin != null) {
            ProtocolUtils.writeUUID(byteBuf, origin);
        }
    }
    
    @Override
    public boolean handle(SessionHandler handler) {
        return handler.handle(this);
    }
    
    public String getKey() {
        return key;
    }
    
    public CompoundTag getValue() {
        return value;
    }
    
    public boolean isPersistent() {
        return persistent;
    }
    
    public UUID getOrigin() {
        return origin;
    }
    
    public void setOrigin(UUID origin) {
        this.origin = origin;
    }
}