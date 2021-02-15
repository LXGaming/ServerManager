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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IntentPacket implements Packet {
    
    public static final int ARRAY_LENGTH = 64;
    public static final int STRING_LENGTH = 255;
    
    private Set<String> intents;
    
    public IntentPacket() {
    }
    
    public IntentPacket(Set<String> intents) {
        this.intents = intents;
    }
    
    @Override
    public void decode(ByteBuf byteBuf) {
        String[] array = ProtocolUtils.readStringArray(byteBuf, ARRAY_LENGTH, STRING_LENGTH);
        this.intents = new HashSet<>(array.length);
        Collections.addAll(this.intents, array);
    }
    
    @Override
    public void encode(ByteBuf byteBuf) {
        Preconditions.checkNotNull(intents, "intents");
        String[] array = intents.toArray(new String[0]);
        ProtocolUtils.writeStringArray(byteBuf, array);
    }
    
    @Override
    public boolean handle(SessionHandler handler) {
        return handler.handle(this);
    }
    
    public Set<String> getIntents() {
        return intents;
    }
}