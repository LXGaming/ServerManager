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

package io.github.lxgaming.servermanager.common.network.packet;

import com.google.common.base.Preconditions;
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.common.entity.InstanceImpl;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public abstract class ListPacket implements Packet {
    
    private ListPacket() {
    }
    
    public static class Request extends ListPacket {
        
        public Request() {
        }
        
        @Override
        public void decode(ByteBuf byteBuf) {
        }
        
        @Override
        public void encode(ByteBuf byteBuf) {
        }
        
        @Override
        public boolean handle(SessionHandler handler) {
            return handler.handle(this);
        }
    }
    
    public static class Response extends ListPacket {
        
        private Collection<Instance> instances;
        
        public Response() {
        }
        
        public Response(Collection<Instance> instances) {
            this.instances = instances;
        }
        
        @Override
        public void decode(ByteBuf byteBuf) {
            int size = ProtocolUtils.readVarInt(byteBuf);
            this.instances = new HashSet<>(size);
            
            for (int index = 0; index < size; index++) {
                UUID id = ProtocolUtils.readUUID(byteBuf);
                String name = ProtocolUtils.readString(byteBuf);
                CompoundTag data = (CompoundTag) ProtocolUtils.readTag(byteBuf);
                
                Instance instance = new InstanceImpl(id, name, data);
                instances.add(instance);
            }
        }
        
        @Override
        public void encode(ByteBuf byteBuf) {
            Preconditions.checkNotNull(instances, "instances");
            ProtocolUtils.writeVarInt(byteBuf, instances.size());
            
            for (Instance instance : instances) {
                ProtocolUtils.writeUUID(byteBuf, instance.getId());
                ProtocolUtils.writeString(byteBuf, instance.getName());
                ProtocolUtils.writeTag(byteBuf, instance.getData());
            }
        }
        
        @Override
        public boolean handle(SessionHandler handler) {
            return handler.handle(this);
        }
        
        public Collection<Instance> getInstances() {
            return instances;
        }
    }
}