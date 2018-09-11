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

package nz.co.lolnet.servermanager.common.manager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.packet.AbstractPacket;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ForwardPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class PacketManager {
    
    private static final Set<Class<? extends AbstractPacket>> PACKET_CLASSES = Toolbox.newHashSet();
    
    public static void buildPackets() {
        registerPacket(CommandPacket.class);
        registerPacket(ForwardPacket.class);
        registerPacket(StatePacket.class);
        registerPacket(StatusPacket.class);
    }
    
    public static boolean process(JsonObject jsonObject) {
        String packetId = Toolbox.parseJson(jsonObject.get("id"), String.class).orElse(null);
        if (Toolbox.isBlank(packetId)) {
            ServerManager.getInstance().getLogger().warn("Received invalid packet");
            return false;
        }
        
        Class<? extends AbstractPacket> packetClass = getPacket(packetId).orElse(null);
        if (packetClass == null) {
            ServerManager.getInstance().getLogger().warn("Cannot find packet class {}", packetId);
            return false;
        }
        
        AbstractPacket packet = Toolbox.parseJson(jsonObject.get("data"), packetClass).orElse(null);
        if (packet == null) {
            ServerManager.getInstance().getLogger().warn("Failed to deserialize packet {}", packetId);
            return false;
        }
        
        ServerManager.getInstance().getLogger().debug("Processing {}: {}", packetClass.getSimpleName(), new Gson().toJson(packet));
        
        try {
            packet.process();
            return true;
        } catch (Exception ex) {
            ServerManager.getInstance().getLogger().error("Encountered an error processing {}::process", "PacketManager", ex);
            return false;
        }
    }
    
    public static void sendPacket(AbstractPacket packet, Consumer<String> consumer) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", packet.getClass().getSimpleName());
        jsonObject.add("data", new Gson().toJsonTree(packet));
        consumer.accept(new Gson().toJson(jsonObject));
    }
    
    public static boolean registerPacket(Class<? extends AbstractPacket> packetClass) {
        if (getPacket(packetClass.getSimpleName()).isPresent()) {
            ServerManager.getInstance().getLogger().warn("{} has already been registered", packetClass.getSimpleName());
            return false;
        }
        
        return getPacketClasses().add(packetClass);
    }
    
    public static Optional<Class<? extends AbstractPacket>> getPacket(String packetId) {
        for (Class<? extends AbstractPacket> packetClass : getPacketClasses()) {
            if (packetClass.getSimpleName().equals(packetId)) {
                return Optional.of(packetClass);
            }
        }
        
        return Optional.empty();
    }
    
    public static Set<Class<? extends AbstractPacket>> getPacketClasses() {
        return PACKET_CLASSES;
    }
}