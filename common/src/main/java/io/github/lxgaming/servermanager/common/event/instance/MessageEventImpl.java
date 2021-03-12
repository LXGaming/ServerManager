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

package io.github.lxgaming.servermanager.common.event.instance;

import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.event.instance.MessageEvent;
import io.github.lxgaming.servermanager.common.event.EventImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public final class MessageEventImpl extends EventImpl implements MessageEvent {
    
    private final UUID instanceId;
    private final String namespace;
    private final String path;
    private final CompoundTag value;
    private final boolean persistent;
    
    public MessageEventImpl(@NonNull Platform platform, @NonNull UUID instanceId, @NonNull String namespace, @NonNull String path, @NonNull CompoundTag value, boolean persistent) {
        super(platform);
        this.instanceId = instanceId;
        this.namespace = namespace;
        this.path = path;
        this.value = value;
        this.persistent = persistent;
    }
    
    @Override
    public @NonNull UUID getInstanceId() {
        return instanceId;
    }
    
    @Override
    public @NonNull String getNamespace() {
        return namespace;
    }
    
    @Override
    public @NonNull String getPath() {
        return path;
    }
    
    @Override
    public @NonNull CompoundTag getValue() {
        return value;
    }
    
    @Override
    public boolean isPersistent() {
        return persistent;
    }
}