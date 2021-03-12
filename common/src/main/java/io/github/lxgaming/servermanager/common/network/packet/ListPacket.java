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
import com.google.common.collect.Sets;
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.entity.InstanceImpl;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
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
        
        private Instance instance;
        private Collection<Instance> instances;
        
        public Response() {
        }
        
        public Response(Instance instance, Collection<Instance> instances) {
            this.instance = instance;
            this.instances = instances;
        }
        
        @Override
        public void decode(ByteBuf byteBuf) {
            this.instance = decodeInstance(byteBuf, Platform.SERVER);
            int size = ProtocolUtils.readVarInt(byteBuf);
            this.instances = Sets.newHashSetWithExpectedSize(size);
            for (int index = 0; index < size; index++) {
                Instance instance = decodeInstance(byteBuf, Platform.CLIENT);
                instances.add(instance);
            }
        }
        
        @Override
        public void encode(ByteBuf byteBuf) {
            Preconditions.checkNotNull(instance, "instance");
            Preconditions.checkNotNull(instances, "instances");
            encodeInstance(byteBuf, instance);
            ProtocolUtils.writeVarInt(byteBuf, instances.size());
            for (Instance instance : instances) {
                encodeInstance(byteBuf, instance);
            }
        }
        
        @Override
        public boolean handle(SessionHandler handler) {
            return handler.handle(this);
        }
        
        private Instance decodeInstance(ByteBuf byteBuf, Platform platform) {
            UUID id = ProtocolUtils.readUUID(byteBuf);
            String name = ProtocolUtils.readString(byteBuf);
            CompoundTag data = (CompoundTag) ProtocolUtils.readTag(byteBuf);
            return new InstanceImpl(id, name, platform, data);
        }
        
        private void encodeInstance(ByteBuf byteBuf, Instance instance) {
            ProtocolUtils.writeUUID(byteBuf, instance.getId());
            ProtocolUtils.writeString(byteBuf, instance.getName());
            ProtocolUtils.writeTag(byteBuf, instance.getData());
        }
        
        public Instance getInstance() {
            return instance;
        }
        
        public Collection<Instance> getInstances() {
            return instances;
        }
    }
}