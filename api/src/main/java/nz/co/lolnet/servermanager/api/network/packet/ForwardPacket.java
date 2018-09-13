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

public class ForwardPacket extends AbstractPacket {
    
    private String server;
    private String packetClass;
    private String data;
    
    private ForwardPacket(String server, String packetClass, String data) {
        this.server = server;
        this.packetClass = packetClass;
        this.data = data;
    }
    
    @Override
    public void process(NetworkHandler networkHandler) {
        networkHandler.handleForward(this);
    }
    
    public static ForwardPacket of(String server, String packetClass, String data) {
        return new ForwardPacket(server, packetClass, data);
    }
    
    public String getServer() {
        return server;
    }
    
    public void setServer(String server) {
        this.server = server;
    }
    
    public String getPacketClass() {
        return packetClass;
    }
    
    public void setPacketClass(String packetClass) {
        this.packetClass = packetClass;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
}