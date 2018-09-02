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

package nz.co.lolnet.servermanager.common.data;

import java.util.Collection;
import java.util.Objects;

public class Server {
    
    private long startTime;
    private Platform.State state;
    private double ticksPerSecond;
    private Platform.Type type;
    private Collection<User> users;
    private String version;
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public Platform.State getState() {
        return state;
    }
    
    public void setState(Platform.State state) {
        this.state = state;
    }
    
    public double getTicksPerSecond() {
        return ticksPerSecond;
    }
    
    public void setTicksPerSecond(double ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
    }
    
    public Platform.Type getType() {
        return type;
    }
    
    public void setType(Platform.Type type) {
        this.type = type;
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
    
    @Override
    public int hashCode() {
        return Objects.hash(
                getStartTime(),
                getState(),
                getTicksPerSecond(),
                getType(),
                getUsers(),
                getVersion());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        Server server = (Server) obj;
        return Objects.equals(getStartTime(), server.getStartTime())
                && Objects.equals(getState(), server.getState())
                && Objects.equals(getTicksPerSecond(), server.getTicksPerSecond())
                && Objects.equals(getType(), server.getType())
                && Objects.equals(getUsers(), server.getUsers())
                && Objects.equals(getVersion(), server.getVersion());
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}