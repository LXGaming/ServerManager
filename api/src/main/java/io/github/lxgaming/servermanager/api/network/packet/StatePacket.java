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
import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.network.NetworkHandler;

public class StatePacket extends AbstractPacket {
    
    @Expose
    private Platform.State state;
    
    @Override
    public void process(NetworkHandler networkHandler) {
        networkHandler.handleState(this);
    }
    
    public Platform.State getState() {
        return state;
    }
    
    public void setState(Platform.State state) {
        this.state = state;
    }
}