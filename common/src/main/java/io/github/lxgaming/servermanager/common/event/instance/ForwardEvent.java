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

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.event.EventImpl;
import io.github.lxgaming.servermanager.common.network.Direction;
import io.github.lxgaming.servermanager.common.network.Packet;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.UUID;

public final class ForwardEvent extends EventImpl {
    
    private final UUID instanceId;
    private final List<UUID> instanceIds;
    private final Direction direction;
    private final Packet packet;
    
    public ForwardEvent(@NonNull Platform platform, @NonNull UUID instanceId, @NonNull List<UUID> instanceIds, @NonNull Direction direction, @NonNull Packet packet) {
        super(platform);
        this.instanceId = instanceId;
        this.instanceIds = instanceIds;
        this.direction = direction;
        this.packet = packet;
    }
    
    public @NonNull UUID getInstanceId() {
        return instanceId;
    }
    
    public @NonNull List<UUID> getInstanceIds() {
        return instanceIds;
    }
    
    public @NonNull Direction getDirection() {
        return direction;
    }
    
    public @NonNull Packet getPacket() {
        return packet;
    }
}