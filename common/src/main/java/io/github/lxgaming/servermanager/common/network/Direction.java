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

package io.github.lxgaming.servermanager.common.network;

public enum Direction {
    
    CLIENTBOUND(0, "Clientbound"),
    SERVERBOUND(1, "Serverbound");
    
    private static final Direction[] VALUES = values();
    private final int id;
    private final String name;
    
    Direction(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public static Direction getDirection(int directionId) {
        for (Direction direction : VALUES) {
            if (direction.getId() == directionId) {
                return direction;
            }
        }
        
        return null;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}