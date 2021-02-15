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

import com.google.common.collect.Lists;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.network.packet.HandshakePacket;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.network.packet.HelloPacket;
import io.github.lxgaming.servermanager.common.network.packet.IntentPacket;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.common.network.packet.LoginPacket;
import io.github.lxgaming.servermanager.common.network.packet.StatusPacket;
import io.github.lxgaming.servermanager.common.util.Toolbox;

import java.util.List;

public enum StateRegistry {
    
    HANDSHAKE(0, "Handshake") {
        {
            // Clientbound
            registerPacket(Direction.CLIENTBOUND, DisconnectPacket.class);
            
            // Serverbound
            registerPacket(Direction.SERVERBOUND, HandshakePacket.class);
        }
    },
    
    STATUS(1, "Status") {
        {
            // Clientbound
            registerPacket(Direction.CLIENTBOUND, DisconnectPacket.class);
            registerPacket(Direction.CLIENTBOUND, StatusPacket.class);
            
            // Serverbound
        }
    },
    
    LOGIN(2, "Login") {
        {
            // Clientbound
            registerPacket(Direction.CLIENTBOUND, DisconnectPacket.class);
            registerPacket(Direction.CLIENTBOUND, HelloPacket.class);
            registerPacket(Direction.CLIENTBOUND, LoginPacket.Response.class);
            
            // Serverbound
            registerPacket(Direction.SERVERBOUND, LoginPacket.Request.class);
        }
    },
    
    APPLICATION(3, "Application") {
        {
            // Clientbound
            registerPacket(Direction.CLIENTBOUND, DisconnectPacket.class);
            registerPacket(Direction.CLIENTBOUND, HeartbeatPacket.class);
            registerPacket(Direction.CLIENTBOUND, ListPacket.Response.class);
            
            // Serverbound
            registerPacket(Direction.SERVERBOUND, HeartbeatPacket.class);
            registerPacket(Direction.SERVERBOUND, IntentPacket.class);
            registerPacket(Direction.SERVERBOUND, ListPacket.Request.class);
        }
    },
    
    INSTANCE(4, "Instance") {
        {
            // Clientbound
            registerPacket(Direction.CLIENTBOUND, DisconnectPacket.class);
            registerPacket(Direction.CLIENTBOUND, HeartbeatPacket.class);
            registerPacket(Direction.CLIENTBOUND, ListPacket.Response.class);
            
            // Serverbound
            registerPacket(Direction.SERVERBOUND, HeartbeatPacket.class);
            registerPacket(Direction.SERVERBOUND, IntentPacket.class);
            registerPacket(Direction.SERVERBOUND, ListPacket.Request.class);
        }
    };
    
    private final int id;
    private final String name;
    private final List<Class<? extends Packet>> clientbound;
    private final List<Class<? extends Packet>> serverbound;
    
    StateRegistry(int id, String name) {
        this.id = id;
        this.name = name;
        this.clientbound = Lists.newArrayList();
        this.serverbound = Lists.newArrayList();
    }
    
    public static StateRegistry getStateRegistry(int stateId) {
        for (StateRegistry stateRegistry : StateRegistry.values()) {
            if (stateRegistry.getId() == stateId) {
                return stateRegistry;
            }
        }
        
        return null;
    }
    
    public void registerPacket(Direction direction, Class<? extends Packet> packetClass) {
        List<Class<? extends Packet>> list = getList(direction);
        if (list.contains(packetClass)) {
            throw new IllegalArgumentException(String.format("%s is already registered", Toolbox.getClassSimpleName(packetClass)));
        }
        
        list.add(packetClass);
    }
    
    public Packet createPacket(Direction direction, int packetId) {
        List<Class<? extends Packet>> list = getList(direction);
        if (packetId < 0 || packetId >= list.size()) {
            return null;
        }
        
        Class<? extends Packet> packetClass = list.get(packetId);
        if (packetClass == null) {
            return null;
        }
        
        Packet packet = Toolbox.newInstance(packetClass);
        if (packet == null) {
            throw new NullPointerException(String.format("%s failed to initialize", Toolbox.getClassSimpleName(packetClass)));
        }
        
        return packet;
    }
    
    public int getPacketId(Direction direction, Class<? extends Packet> packetClass) {
        int packetId = getList(direction).indexOf(packetClass);
        if (packetId == -1) {
            throw new IllegalArgumentException(String.format("%s is not registered for %s (%s)", Toolbox.getClassSimpleName(packetClass), this, direction));
        }
        
        return packetId;
    }
    
    private List<Class<? extends Packet>> getList(Direction direction) {
        if (direction == Direction.CLIENTBOUND) {
            return clientbound;
        }
        
        if (direction == Direction.SERVERBOUND) {
            return serverbound;
        }
        
        throw new UnsupportedOperationException(String.format("%s is not supported", direction));
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