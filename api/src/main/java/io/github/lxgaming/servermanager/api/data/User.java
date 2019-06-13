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

package io.github.lxgaming.servermanager.api.data;

import com.google.gson.annotations.Expose;

import java.util.Objects;
import java.util.UUID;

public class User {
    
    @Expose
    private final String name;
    
    @Expose
    private final UUID uniqueId;
    
    @Expose
    private String address;
    
    public User(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }
    
    public String getName() {
        return name;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUniqueId());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        User user = (User) obj;
        return Objects.equals(getName(), user.getName()) && Objects.equals(getUniqueId(), user.getUniqueId());
    }
    
    @Override
    public String toString() {
        return getName() + " (" + getUniqueId() + ")";
    }
}