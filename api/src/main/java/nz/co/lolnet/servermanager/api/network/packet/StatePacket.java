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

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.Platform;

public class StatePacket extends AbstractPacket {
    
    private Platform.State state;
    
    private StatePacket(Platform.State state) {
        this.state = state;
    }
    
    @Override
    public void process() {
        ServerManager.getInstance().getNetworkHandler().handleState(this);
    }
    
    public static StatePacket of(Platform.State state) {
        return new StatePacket(state);
    }
    
    public Platform.State getState() {
        return state;
    }
    
    public void setState(Platform.State state) {
        this.state = state;
    }
}