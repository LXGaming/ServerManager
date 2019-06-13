/*
 * Copyright 2018 Alex Thomson
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

package io.github.lxgaming.servermanager.api;

import java.util.UUID;

public interface Platform {
    
    UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    
    enum State {
        
        CONNECTED("Connected"),
        
        JVM_STARTED("JVM Started"),
        
        CONSTRUCTION("Construction"),
        
        PRE_INITIALIZATION("Pre Initialization"),
        
        INITIALIZATION("Initialization"),
        
        POST_INITIALIZATION("Post Initialization"),
        
        LOAD_COMPLETE("Load Complete"),
        
        SERVER_ABOUT_TO_START("Server About To Start"),
        
        SERVER_STARTING("Server Starting"),
        
        SERVER_STARTED("Server Started"),
        
        SERVER_STOPPING("Server Stopping"),
        
        SERVER_STOPPED("Server Stopped"),
        
        JVM_STOPPED("JVM Stopped"),
        
        DISCONNECTED("Disconnected"),
        
        FROZEN("Frozen"),
        
        UNKNOWN("Unknown");
        
        private final String name;
        
        State(String name) {
            this.name = name;
        }
        
        public boolean isOnline() {
            return this == CONNECTED
                    || this == JVM_STARTED
                    || this == CONSTRUCTION
                    || this == PRE_INITIALIZATION
                    || this == INITIALIZATION
                    || this == POST_INITIALIZATION
                    || this == LOAD_COMPLETE
                    || this == SERVER_ABOUT_TO_START
                    || this == SERVER_STARTING
                    || this == SERVER_STARTED;
        }
        
        public boolean isOffline() {
            return this == DISCONNECTED
                    || this == SERVER_STOPPING
                    || this == SERVER_STOPPED
                    || this == JVM_STOPPED;
        }
        
        public boolean isFrozen() {
            return this == FROZEN;
        }
        
        public boolean isKnown() {
            return !isUnknown();
        }
        
        public boolean isUnknown() {
            return this == UNKNOWN;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    enum Type {
        
        BUNGEECORD("BungeeCord"),
        
        SERVER("Server"),
        
        SPONGE("Sponge"),
        
        VELOCITY("Velocity"),
        
        UNKNOWN("Unknown");
        
        private final String name;
        
        Type(String name) {
            this.name = name;
        }
        
        public boolean isBungeeCord() {
            return this == BUNGEECORD;
        }
        
        public boolean isServer() {
            return this == SERVER;
        }
        
        public boolean isSponge() {
            return this == SPONGE;
        }
        
        public boolean isVelocity() {
            return this == VELOCITY;
        }
        
        public boolean isKnown() {
            return !isUnknown();
        }
        
        public boolean isUnknown() {
            return this == UNKNOWN;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}