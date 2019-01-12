/*
 * Copyright 2019 lolnet.co.nz
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

import com.google.gson.annotations.Expose;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;

public class MessagePacket extends AbstractPacket {
    
    @Expose
    private final String message;
    
    @Expose
    private final String permission;
    
    @Expose
    private final Position position;
    
    public MessagePacket(String message, String permission) {
        this(message, permission, Position.CHAT);
    }
    
    public MessagePacket(String message, String permission, Position position) {
        this.message = message;
        this.permission = permission;
        this.position = position;
    }
    
    @Override
    public void process(NetworkHandler networkHandler) {
        networkHandler.handleMessage(this);
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public enum Position {
        
        ACTION_BAR("Action Bar"),
        
        CHAT("Chat");
        
        private final String friendlyName;
        
        Position(String friendlyName) {
            this.friendlyName = friendlyName;
        }
        
        public String getFriendlyName() {
            return friendlyName;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}