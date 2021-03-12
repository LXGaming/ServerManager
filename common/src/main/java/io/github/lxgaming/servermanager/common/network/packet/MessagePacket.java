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
    
    public static final int NAMESPACE_LENGTH = 16;
    public static final int PATH_LENGTH = 255;
    
    private String namespace;
    private String path;
    private CompoundTag value;
    private boolean persistent;
    private UUID instanceId;
    
    public MessagePacket() {
    }
    
    public MessagePacket(String namespace, String path, CompoundTag value) {
        this(namespace, path, value, false);
    }
    
    public MessagePacket(String namespace, String path, CompoundTag value, boolean persistent) {
        this(namespace, path, value, persistent, null);
    }
    
    public MessagePacket(String namespace, String path, CompoundTag value, boolean persistent, UUID instanceId) {
        this.namespace = namespace;
        this.path = path;
        this.value = value;
        this.persistent = persistent;
        this.instanceId = instanceId;
    }
    
    @Override
    public void decode(ByteBuf byteBuf) {
        this.namespace = ProtocolUtils.readString(byteBuf, NAMESPACE_LENGTH);
        this.path = ProtocolUtils.readString(byteBuf, PATH_LENGTH);
        this.value = (CompoundTag) ProtocolUtils.readTag(byteBuf);
        this.persistent = byteBuf.readBoolean();
        if (byteBuf.readBoolean()) {
            this.instanceId = ProtocolUtils.readUUID(byteBuf);
        }
    }
    
    @Override
    public void encode(ByteBuf byteBuf) {
        Preconditions.checkNotNull(namespace, "namespace");
        Preconditions.checkNotNull(path, "path");
        Preconditions.checkNotNull(value, "value");
        Preconditions.checkState(namespace.length() <= NAMESPACE_LENGTH, "Namespace exceeds maximum length");
        Preconditions.checkState(path.length() <= PATH_LENGTH, "Path exceeds maximum length");
        ProtocolUtils.writeString(byteBuf, namespace);
        ProtocolUtils.writeString(byteBuf, path);
        ProtocolUtils.writeTag(byteBuf, value);
        byteBuf.writeBoolean(persistent);
        byteBuf.writeBoolean(instanceId != null);
        if (instanceId != null) {
            ProtocolUtils.writeUUID(byteBuf, instanceId);
        }
    }
    
    @Override
    public boolean handle(SessionHandler handler) {
        return handler.handle(this);
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public String getPath() {
        return path;
    }
    
    public CompoundTag getValue() {
        return value;
    }
    
    public boolean isPersistent() {
        return persistent;
    }
    
    public UUID getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(UUID instanceId) {
        this.instanceId = instanceId;
    }
}