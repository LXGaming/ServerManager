/*
 * Copyright 2019 Alex Thomson
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
import io.github.lxgaming.servermanager.api.data.User;
import io.github.lxgaming.servermanager.api.network.NetworkHandler;

public abstract class UserPacket extends AbstractPacket {
    
    @Expose
    private final User user;
    
    private UserPacket(User user) {
        this.user = user;
    }
    
    @Override
    public void process(NetworkHandler networkHandler) {
        if (this instanceof UserPacket.Add) {
            networkHandler.handleUserAdd((UserPacket.Add) this);
        }
        
        if (this instanceof UserPacket.Remove) {
            networkHandler.handleUserRemove((UserPacket.Remove) this);
        }
    }
    
    public User getUser() {
        return user;
    }
    
    public static class Add extends UserPacket {
        
        public Add(User user) {
            super(user);
        }
    }
    
    public static class Remove extends UserPacket {
        
        public Remove(User user) {
            super(user);
        }
    }
}