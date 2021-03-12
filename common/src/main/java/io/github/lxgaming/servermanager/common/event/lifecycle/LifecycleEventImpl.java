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

package io.github.lxgaming.servermanager.common.event.lifecycle;

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.event.lifecycle.LifecycleEvent;
import io.github.lxgaming.servermanager.common.event.EventImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LifecycleEventImpl extends EventImpl implements LifecycleEvent {
    
    private LifecycleEventImpl(@NonNull Platform platform) {
        super(platform);
    }
    
    public static final class Initialize extends LifecycleEventImpl implements LifecycleEvent.Initialize {
        
        public Initialize(@NonNull Platform platform) {
            super(platform);
        }
    }
    
    public static final class Shutdown extends LifecycleEventImpl implements LifecycleEvent.Shutdown {
        
        public Shutdown(@NonNull Platform platform) {
            super(platform);
        }
    }
}