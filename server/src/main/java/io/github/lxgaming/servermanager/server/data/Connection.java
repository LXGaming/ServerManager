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

package io.github.lxgaming.servermanager.server.data;

import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.data.Implementation;
import io.github.lxgaming.servermanager.api.data.Setting;

import java.util.Objects;

public class Connection extends Implementation {
    
    private final Implementation.Data data = new Implementation.Data();
    private long lastPacketTime;
    private Setting setting;
    
    public Connection(String id, String name, Platform.Type type) {
        super(id, name, type);
    }
    
    public Implementation.Data getData() {
        return data;
    }
    
    public long getLastPacketTime() {
        return lastPacketTime;
    }
    
    public void setLastPacketTime(long lastPacketTime) {
        this.lastPacketTime = lastPacketTime;
    }
    
    public Setting getSetting() {
        return setting;
    }
    
    public void setSetting(Setting setting) {
        this.setting = setting;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Connection connection = (Connection) obj;
        return Objects.equals(getId(), connection.getId());
    }
    
    @Override
    public String toString() {
        return getId();
    }
}