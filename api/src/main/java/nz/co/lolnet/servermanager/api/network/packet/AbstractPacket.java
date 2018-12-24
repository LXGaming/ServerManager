/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.servermanager.api.network.packet;

import nz.co.lolnet.servermanager.api.network.Packet;

public abstract class AbstractPacket implements Packet {
    
    private String forwardTo;
    private String sender;
    private Type type;
    
    @Override
    public String getForwardTo() {
        return forwardTo;
    }
    
    @Override
    public void setForwardTo(String forwardTo) {
        this.forwardTo = forwardTo;
    }
    
    @Override
    public String getSender() {
        return sender;
    }
    
    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    @Override
    public Type getType() {
        return type;
    }
    
    @Override
    public void setType(Type type) {
        this.type = type;
    }
}