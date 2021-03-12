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

package io.github.lxgaming.servermanager.common.event;

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

public class EventImpl implements Event {
    
    private final Platform platform;
    
    protected EventImpl(@NonNull Platform platform) {
        this.platform = platform;
    }
    
    @Override
    public final @NonNull Platform getPlatform() {
        return platform;
    }
}