/*
 * Copyright 2018 Alex Thomson
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

package io.github.lxgaming.servermanager.api.network.packet;

import com.google.gson.annotations.Expose;
import io.github.lxgaming.servermanager.api.network.Packet;

public abstract class AbstractPacket implements Packet {
    
    @Expose
    private String forwardTo;
    
    @Expose
    private String sender;
    
    @Expose
    private Type type;
    
    @Override
    public final String getForwardTo() {
        return forwardTo;
    }
    
    @Override
    public final void setForwardTo(String forwardTo) {
        this.forwardTo = forwardTo;
    }
    
    @Override
    public final String getSender() {
        return sender;
    }
    
    @Override
    public final void setSender(String sender) {
        this.sender = sender;
    }
    
    @Override
    public final Type getType() {
        return type;
    }
    
    @Override
    public final void setType(Type type) {
        this.type = type;
    }
}