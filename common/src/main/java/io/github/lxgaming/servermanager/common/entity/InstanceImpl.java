/*
 * Copyright 2021 Alex Thomson
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

package io.github.lxgaming.servermanager.common.entity;

import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.entity.Instance;

import java.util.Objects;
import java.util.UUID;

public class InstanceImpl implements Instance {
    
    private final UUID id;
    private final String name;
    private final CompoundTag data;
    
    public InstanceImpl(UUID id, String name) {
        this(id, name, new CompoundTag());
    }
    
    public InstanceImpl(UUID id, String name, CompoundTag data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }
    
    @Override
    public UUID getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public CompoundTag getData() {
        return data;
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
        
        Instance instance = (Instance) obj;
        return Objects.equals(getId(), instance.getId());
    }
}