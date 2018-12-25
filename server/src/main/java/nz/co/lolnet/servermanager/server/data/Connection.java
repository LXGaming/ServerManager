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

package nz.co.lolnet.servermanager.server.data;

import nz.co.lolnet.servermanager.api.data.ServerInfo;
import nz.co.lolnet.servermanager.api.data.Setting;

public class Connection {
    
    private final String channel;
    private final String name;
    private long lastPacketTime;
    private ServerInfo serverInfo;
    private Setting setting;
    
    public Connection(String channel, String name) {
        this.channel = channel;
        this.name = name;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public String getName() {
        return name;
    }
    
    public long getLastPacketTime() {
        return lastPacketTime;
    }
    
    public void setLastPacketTime(long lastPacketTime) {
        this.lastPacketTime = lastPacketTime;
    }
    
    public ServerInfo getServerInfo() {
        return serverInfo;
    }
    
    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
    
    public Setting getSetting() {
        return setting;
    }
    
    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}