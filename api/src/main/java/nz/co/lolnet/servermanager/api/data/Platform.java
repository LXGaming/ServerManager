/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.servermanager.api.data;

public interface Platform {
    
    enum State {
        
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
        
        GAME_STOPPING("Game Stopping"),
        
        GAME_STOPPED("Game Stopped"),
        
        JVM_STOPPED("JVM Stopped"),
        
        UNKNOWN("Unknown");
        
        private final String friendlyName;
        
        State(String friendlyName) {
            this.friendlyName = friendlyName;
        }
        
        public String getFriendlyName() {
            return friendlyName;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    enum Type {
        
        BUKKIT("Bukkit"),
        
        BUNGEE("BungeeCord"),
        
        SERVER("Server"),
        
        SPONGE("Sponge"),
        
        VELOCITY("Velocity"),
        
        UNKNOWN("Unknown");
        
        private final String friendlyName;
        
        Type(String friendlyName) {
            this.friendlyName = friendlyName;
        }
        
        public String getFriendlyName() {
            return friendlyName;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}