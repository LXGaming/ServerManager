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

package io.github.lxgaming.servermanager.api.network.packet;

import com.google.gson.annotations.Expose;
import io.github.lxgaming.servermanager.api.data.Implementation;
import io.github.lxgaming.servermanager.api.network.NetworkHandler;

import java.util.Collection;
import java.util.Map;

public abstract class ListPacket extends AbstractPacket {
    
    @Override
    public void process(NetworkHandler networkHandler) {
        if (this instanceof ListPacket.Basic) {
            networkHandler.handleListBasic((ListPacket.Basic) this);
        }
        
        if (this instanceof ListPacket.Full) {
            networkHandler.handleListFull((ListPacket.Full) this);
        }
    }
    
    public static class Basic extends ListPacket {
        
        @Expose
        private Collection<Implementation> implementations;
        
        public Collection<Implementation> getImplementations() {
            return implementations;
        }
        
        public void setImplementations(Collection<Implementation> implementations) {
            this.implementations = implementations;
        }
    }
    
    public static class Full extends ListPacket {
        
        @Expose
        private Map<Implementation, Implementation.Data> implementations;
        
        public Map<Implementation, Implementation.Data> getImplementations() {
            return implementations;
        }
        
        public void setImplementations(Map<Implementation, Implementation.Data> implementations) {
            this.implementations = implementations;
        }
    }
}