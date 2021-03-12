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

import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.event.instance.ListEvent;
import io.github.lxgaming.servermanager.common.event.EventImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

public final class ListEventImpl extends EventImpl implements ListEvent {
    
    private final Instance instance;
    private final Collection<Instance> instances;
    
    public ListEventImpl(@NonNull Platform platform, @NonNull Instance instance, @NonNull Collection<Instance> instances) {
        super(platform);
        this.instance = instance;
        this.instances = instances;
    }
    
    @Override
    public @NonNull Instance getInstance() {
        return instance;
    }
    
    @Override
    public @NonNull Collection<Instance> getInstances() {
        return instances;
    }
}