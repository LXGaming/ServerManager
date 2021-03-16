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

package io.github.lxgaming.servermanager.api.entity;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum Health {
    
    HEALTHY(1, "Healthy"),
    UNHEALTHY(2, "Unhealthy"),
    UNKNOWN(0, "Unknown");
    
    private static final Health[] VALUES = values();
    private final int id;
    private final String name;
    
    Health(int id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
    
    public static @NonNull Health getHealth(int healthId) {
        for (Health health : VALUES) {
            if (health.getId() == healthId) {
                return health;
            }
        }
        
        return UNKNOWN;
    }
    
    public int getId() {
        return id;
    }
    
    public @NonNull String getName() {
        return name;
    }
    
    @Override
    public @NonNull String toString() {
        return name().toLowerCase();
    }
}