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
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class CommandPacket implements Packet {
    
    public static final int COMMAND_LENGTH = 512;
    public static final int USERNAME_LENGTH = 64;
    
    private UUID instanceId;
    private String command;
    private String username;
    
    public CommandPacket() {
    }
    
    public CommandPacket(UUID instanceId, String command, String username) {
        this.instanceId = instanceId;
        this.command = command;
        this.username = username;
    }
    
    @Override
    public void decode(ByteBuf byteBuf) {
        this.instanceId = ProtocolUtils.readUUID(byteBuf);
        this.command = ProtocolUtils.readString(byteBuf, COMMAND_LENGTH);
        this.username = ProtocolUtils.readString(byteBuf, USERNAME_LENGTH);
    }
    
    @Override
    public void encode(ByteBuf byteBuf) {
        Preconditions.checkNotNull(instanceId, "instanceId");
        Preconditions.checkNotNull(command, "command");
        Preconditions.checkNotNull(username, "username");
        Preconditions.checkState(command.length() <= COMMAND_LENGTH, "Command exceeds maximum length");
        Preconditions.checkState(username.length() <= USERNAME_LENGTH, "Username exceeds maximum length");
        ProtocolUtils.writeUUID(byteBuf, instanceId);
        ProtocolUtils.writeString(byteBuf, command);
        ProtocolUtils.writeString(byteBuf, username);
    }
    
    @Override
    public boolean handle(SessionHandler handler) {
        return handler.handle(this);
    }
    
    public UUID getInstanceId() {
        return instanceId;
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getUsername() {
        return username;
    }
}