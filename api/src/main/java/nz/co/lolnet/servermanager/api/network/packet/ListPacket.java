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

import nz.co.lolnet.servermanager.api.network.NetworkHandler;

import java.util.Set;

public class ListPacket extends AbstractPacket {
    
    private Set<String> servers;
    
    @Override
    public void process(NetworkHandler networkHandler) {
        networkHandler.handleList(this);
    }
    
    public Set<String> getServers() {
        return servers;
    }
    
    public void setServers(Set<String> servers) {
        this.servers = servers;
    }
}