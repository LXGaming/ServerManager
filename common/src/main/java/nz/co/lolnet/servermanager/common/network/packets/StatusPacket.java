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

package nz.co.lolnet.servermanager.common.network.packets;

import nz.co.lolnet.servermanager.common.AbstractServerManager;
import nz.co.lolnet.servermanager.common.data.Server;

public class StatusPacket extends AbstractPacket {
    
    private Server server;
    
    private StatusPacket(Server server) {
        this.server = server;
    }
    
    @Override
    public void process() {
        AbstractServerManager.getInstance().getNetworkHandler().handleStatus(this);
    }
    
    public static StatusPacket of(Server server) {
        return new StatusPacket(server);
    }
    
    public Server getServer() {
        return server;
    }
    
    public StatusPacket server(Server server) {
        this.server = server;
        return this;
    }
}