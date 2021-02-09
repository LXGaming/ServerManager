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

import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

public class HelloPacket implements Packet {
    
    private int compressionThreshold;
    private boolean encrypted;
    
    public HelloPacket() {
    }
    
    public HelloPacket(int compressionThreshold, boolean encrypted) {
        this.compressionThreshold = compressionThreshold;
        this.encrypted = encrypted;
    }
    
    @Override
    public void decode(ByteBuf byteBuf) {
        this.compressionThreshold = ProtocolUtils.readVarInt(byteBuf);
        this.encrypted = byteBuf.readBoolean();
    }
    
    @Override
    public void encode(ByteBuf byteBuf) {
        ProtocolUtils.writeVarInt(byteBuf, compressionThreshold);
        byteBuf.writeBoolean(encrypted);
    }
    
    @Override
    public boolean handle(SessionHandler handler) {
        return handler.handle(this);
    }
    
    public int getCompressionThreshold() {
        return compressionThreshold;
    }
    
    public boolean isEncrypted() {
        return encrypted;
    }
}