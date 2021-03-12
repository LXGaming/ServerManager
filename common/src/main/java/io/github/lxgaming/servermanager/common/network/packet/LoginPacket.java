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
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public abstract class LoginPacket implements Packet {
    
    private LoginPacket() {
    }
    
    public static class Request extends LoginPacket {
        
        public static final int NAME_LENGTH = 255;
        public static final int PATH_LENGTH = 4096;
        
        private UUID id;
        private String name;
        private Platform platform;
        private String path;
        
        public Request() {
        }
        
        public Request(UUID id, String name, Platform platform) {
            this(id, name, platform, null);
        }
        
        public Request(UUID id, String name, Platform platform, String path) {
            this.id = id;
            this.name = name;
            this.platform = platform;
            this.path = path;
        }
        
        @Override
        public void decode(ByteBuf byteBuf) {
            this.id = ProtocolUtils.readUUID(byteBuf);
            this.name = ProtocolUtils.readString(byteBuf, NAME_LENGTH);
            int platformId = ProtocolUtils.readVarInt(byteBuf);
            this.platform = Platform.getPlatform(platformId);
            if (byteBuf.readBoolean()) {
                this.path = ProtocolUtils.readString(byteBuf, PATH_LENGTH);
            }
        }
        
        @Override
        public void encode(ByteBuf byteBuf) {
            Preconditions.checkNotNull(id, "id");
            Preconditions.checkNotNull(name, "name");
            Preconditions.checkNotNull(platform, "platform");
            Preconditions.checkState(name.length() <= NAME_LENGTH, "Name exceeds maximum length");
            Preconditions.checkState(path == null || path.length() <= PATH_LENGTH, "Path exceeds maximum length");
            ProtocolUtils.writeUUID(byteBuf, id);
            ProtocolUtils.writeString(byteBuf, name);
            ProtocolUtils.writeVarInt(byteBuf, platform.getId());
            byteBuf.writeBoolean(path != null);
            if (path != null) {
                ProtocolUtils.writeString(byteBuf, path);
            }
        }
        
        @Override
        public boolean handle(SessionHandler handler) {
            return handler.handle(this);
        }
        
        public UUID getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public Platform getPlatform() {
            return platform;
        }
        
        public String getPath() {
            return path;
        }
    }
    
    public static class Response extends LoginPacket {
        
        private UUID id;
        
        public Response() {
        }
        
        public Response(UUID id) {
            this.id = id;
        }
        
        @Override
        public void decode(ByteBuf byteBuf) {
            this.id = ProtocolUtils.readUUID(byteBuf);
        }
        
        @Override
        public void encode(ByteBuf byteBuf) {
            Preconditions.checkNotNull(id, "id");
            ProtocolUtils.writeUUID(byteBuf, id);
        }
        
        @Override
        public boolean handle(SessionHandler handler) {
            return handler.handle(this);
        }
        
        public UUID getId() {
            return id;
        }
    }
}