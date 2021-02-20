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

package io.github.lxgaming.servermanager.common.event.connection;

import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.event.connection.MessageEvent;
import io.github.lxgaming.servermanager.common.event.EventImpl;

public class MessageEventImpl extends EventImpl implements MessageEvent {
    
    private final Instance instance;
    private final String namespace;
    private final String path;
    private final CompoundTag value;
    private final boolean persistent;
    
    public MessageEventImpl(Instance instance, String namespace, String path, CompoundTag value, boolean persistent) {
        this.instance = instance;
        this.namespace = namespace;
        this.path = path;
        this.value = value;
        this.persistent = persistent;
    }
    
    @Override
    public Instance getInstance() {
        return instance;
    }
    
    @Override
    public String getNamespace() {
        return namespace;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public CompoundTag getValue() {
        return value;
    }
    
    @Override
    public boolean isPersistent() {
        return persistent;
    }
}