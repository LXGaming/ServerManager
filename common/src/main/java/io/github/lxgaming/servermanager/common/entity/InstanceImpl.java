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
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Health;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.entity.State;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.UUID;

public class InstanceImpl implements Instance {
    
    private final UUID id;
    private final String name;
    private final Platform platform;
    private final CompoundTag data;
    private State state;
    
    public InstanceImpl(@NonNull UUID id, @NonNull String name, @NonNull Platform platform) {
        this(id, name, platform, new CompoundTag(), State.UNKNOWN);
    }
    
    public InstanceImpl(@NonNull UUID id, @NonNull String name, @NonNull Platform platform, @NonNull CompoundTag data, @NonNull State state) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.data = data;
        this.state = state;
    }
    
    @Override
    public @NonNull UUID getId() {
        return id;
    }
    
    @Override
    public @NonNull String getName() {
        return name;
    }
    
    @Override
    public @NonNull Platform getPlatform() {
        return platform;
    }
    
    @Override
    public @NonNull CompoundTag getData() {
        return data;
    }
    
    @Override
    public @NonNull Health getHealth() {
        CompoundTag compound = (CompoundTag) getData().get(ServerManager.ID);
        int healthId = compound != null ? compound.getInt("health") : 0;
        return Health.getHealth(healthId);
    }
    
    @Override
    public @NonNull State getState() {
        return state;
    }
    
    public void setState(@NonNull State state) {
        this.state = state;
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