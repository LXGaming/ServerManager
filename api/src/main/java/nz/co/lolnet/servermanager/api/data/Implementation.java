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

package nz.co.lolnet.servermanager.api.data;

import com.google.gson.annotations.Expose;
import nz.co.lolnet.servermanager.api.Platform;

import java.util.Collection;

public class Implementation {
    
    @Expose
    private final String id;
    
    @Expose
    private final String name;
    
    @Expose
    private final Platform.Type type;
    
    public Implementation(String id, String name, Platform.Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Platform.Type getType() {
        return type;
    }
    
    public static class Data {
        
        @Expose
        private Long lastTickTime;
        
        @Expose
        private Long startTime;
        
        @Expose
        private Platform.State state;
        
        @Expose
        private Double ticksPerSecond;
        
        @Expose
        private Collection<User> users;
        
        @Expose
        private String version;
        
        public Long getLastTickTime() {
            return lastTickTime;
        }
        
        public void setLastTickTime(Long lastTickTime) {
            this.lastTickTime = lastTickTime;
        }
        
        public Long getStartTime() {
            return startTime;
        }
        
        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }
        
        public Platform.State getState() {
            return state;
        }
        
        public void setState(Platform.State state) {
            this.state = state;
        }
        
        public Double getTicksPerSecond() {
            return ticksPerSecond;
        }
        
        public void setTicksPerSecond(Double ticksPerSecond) {
            this.ticksPerSecond = ticksPerSecond;
        }
        
        public Collection<User> getUsers() {
            return users;
        }
        
        public void setUsers(Collection<User> users) {
            this.users = users;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
    }
}