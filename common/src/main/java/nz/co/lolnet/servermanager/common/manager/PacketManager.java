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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ListPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.SettingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.api.network.packet.UserPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class PacketManager {
    
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .enableComplexMapKeySerialization()
            .create();
    
    private static final Set<NetworkHandler> NETWORK_HANDLERS = Toolbox.newHashSet();
    private static final Set<Class<? extends NetworkHandler>> NETWORK_HANDLER_CLASSES = Toolbox.newHashSet();
    private static final Set<Class<? extends Packet>> PACKET_CLASSES = Toolbox.newHashSet();
    
    public static void buildPackets() {
        registerPacket(CommandPacket.class);
        registerPacket(ListPacket.Basic.class);
        registerPacket(ListPacket.Full.class);
        registerPacket(PingPacket.class);
        registerPacket(SettingPacket.class);
        registerPacket(StatePacket.class);
        registerPacket(StatusPacket.class);
        registerPacket(UserPacket.Add.class);
        registerPacket(UserPacket.Remove.class);
    }
    
    public static void process(JsonObject jsonObject) {
        String packetClassName = Toolbox.parseJson(jsonObject.get("class"), String.class).orElse(null);
        if (Toolbox.isBlank(packetClassName)) {
            ServerManager.getInstance().getLogger().warn("Received invalid packet");
            return;
        }
        
        Class<? extends Packet> packetClass = getPacketClass(packetClassName).orElse(null);
        if (packetClass == null) {
            ServerManager.getInstance().getLogger().warn("Received packet with unknown class {}", packetClassName);
            return;
        }
        
        Packet packet = Toolbox.parseJson(jsonObject.get("data"), packetClass).orElse(null);
        if (packet == null) {
            ServerManager.getInstance().getLogger().warn("Failed to deserialize packet {}", packetClass.getName());
            return;
        }
        
        ServerManager.getInstance().getLogger().debug("Processing {} ({}) from {}", Toolbox.getClassSimpleName(packetClass), packet.getType(), packet.getSender());
        for (NetworkHandler networkHandler : NETWORK_HANDLERS) {
            try {
                if (networkHandler.handle(packet)) {
                    packet.process(networkHandler);
                }
            } catch (Throwable throwable) {
                ServerManager.getInstance().getLogger().error("Encountered an error processing {}::process", networkHandler.getClass().getName(), throwable);
            }
        }
    }
    
    public static void sendPacket(String id, Packet packet, BiConsumer<String, String> consumer) {
        if (Toolbox.isBlank(packet.getSender())) {
            ServerManager.getInstance().getConfig()
                    .map(Config::getName)
                    .map(name -> Toolbox.createId(ServerManager.getInstance().getPlatformType(), name))
                    .ifPresent(packet::setSender);
        }
        
        if (packet.getType() == null) {
            ServerManager.getInstance().getLogger().error("Cannot send packet without type");
            return;
        }
        
        sendPacket(id, packet.getClass(), GSON.toJsonTree(packet), consumer);
    }
    
    private static void sendPacket(String id, Class<? extends Packet> packetClass, JsonElement jsonElement, BiConsumer<String, String> consumer) {
        if (!PACKET_CLASSES.contains(packetClass)) {
            ServerManager.getInstance().getLogger().error("Can't serialize unregistered packet", packetClass.getSimpleName());
            return;
        }
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("class", packetClass.getName());
        jsonObject.add("data", jsonElement);
        consumer.accept(Toolbox.createChannel(id), GSON.toJson(jsonObject));
    }
    
    public static boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        if (NETWORK_HANDLER_CLASSES.contains(networkHandlerClass)) {
            ServerManager.getInstance().getLogger().warn("{} is already registered", networkHandlerClass.getSimpleName());
            return false;
        }
        
        NETWORK_HANDLER_CLASSES.add(networkHandlerClass);
        NetworkHandler networkHandler = Toolbox.newInstance(networkHandlerClass).orElse(null);
        if (networkHandler == null) {
            ServerManager.getInstance().getLogger().error("{} failed to initialize", networkHandlerClass.getSimpleName());
            return false;
        }
        
        NETWORK_HANDLERS.add(networkHandler);
        ServerManager.getInstance().getLogger().debug("{} registered", networkHandlerClass.getSimpleName());
        return true;
    }
    
    public static boolean registerPacket(Class<? extends Packet> packetClass) {
        if (PACKET_CLASSES.contains(packetClass)) {
            ServerManager.getInstance().getLogger().warn("{} is already registered", packetClass.getSimpleName());
            return false;
        }
        
        return PACKET_CLASSES.add(packetClass);
    }
    
    public static Optional<Class<? extends Packet>> getPacketClass(String packetClassName) {
        for (Class<? extends Packet> packetClass : PACKET_CLASSES) {
            if (packetClass.getName().equals(packetClassName)) {
                return Optional.of(packetClass);
            }
        }
        
        return Optional.empty();
    }
}