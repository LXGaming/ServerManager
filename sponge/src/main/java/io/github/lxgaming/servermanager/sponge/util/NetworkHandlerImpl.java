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

package io.github.lxgaming.servermanager.sponge.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerHangWatchdog;
import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.data.Implementation;
import io.github.lxgaming.servermanager.api.data.User;
import io.github.lxgaming.servermanager.api.network.AbstractNetworkHandler;
import io.github.lxgaming.servermanager.api.network.Packet;
import io.github.lxgaming.servermanager.api.network.packet.CommandPacket;
import io.github.lxgaming.servermanager.api.network.packet.MessagePacket;
import io.github.lxgaming.servermanager.api.network.packet.PingPacket;
import io.github.lxgaming.servermanager.api.network.packet.SettingPacket;
import io.github.lxgaming.servermanager.api.network.packet.StatePacket;
import io.github.lxgaming.servermanager.api.network.packet.StatusPacket;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.sponge.ServerManagerImpl;
import io.github.lxgaming.servermanager.sponge.SpongePlugin;
import io.github.lxgaming.servermanager.sponge.interfaces.server.dedicated.IMixinServerHangWatchdog;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.common.SpongeImpl;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.stream.Collectors;

public class NetworkHandlerImpl extends AbstractNetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        return packet.getType().equals(Packet.Type.REQUEST);
    }
    
    @Override
    public void handleCommand(CommandPacket packet) {
        if (Toolbox.isBlank(packet.getCommand())) {
            return;
        }
        
        ServerManagerImpl.getInstance().getLogger().info("Processing {} for {}", packet.getCommand(), packet.getUser());
        if (packet.getCommand().equals("servermanager:terminate")) {
            Toolbox.cast(new ServerHangWatchdog(Toolbox.cast(Sponge.getServer(), DedicatedServer.class)), IMixinServerHangWatchdog.class).scheduleHalt();
            return;
        }
        
        Task.builder().execute(() -> {
            Sponge.getCommandManager().process(new SpongeCommandSource(), packet.getCommand());
        }).submit(SpongePlugin.getInstance().getPluginContainer());
    }
    
    @Override
    public void handleMessage(MessagePacket packet) {
        if (Toolbox.isBlank(packet.getMessage())) {
            return;
        }
        
        ChatType chatType = SpongeToolbox.getMessagePosition(packet.getPosition());
        Text text = SpongeToolbox.deserializeLegacy(packet.getMessage());
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (Toolbox.isBlank(packet.getPermission()) || player.hasPermission(packet.getPermission())) {
                player.sendMessage(chatType, text);
            }
        }
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void handleSetting(SettingPacket packet) {
        ServerManagerImpl.getInstance().setSetting(packet.getSetting());
    }
    
    @Override
    public void handleState(StatePacket packet) {
        packet.setState(SpongePlugin.getInstance().getState());
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        Implementation.Data data = new Implementation.Data();
        data.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        
        if (SpongePlugin.getInstance() != null) {
            data.setState(SpongePlugin.getInstance().getState());
        } else {
            data.setState(Platform.State.CONSTRUCTION);
        }
        
        if (SpongeImpl.isInitialized() && Sponge.isServerAvailable()) {
            data.setLastTickTime(((MinecraftServer) Sponge.getServer()).getCurrentTime());
            data.setTicksPerSecond(Sponge.getServer().getTicksPerSecond());
            data.setUsers(Sponge.getServer().getOnlinePlayers().stream()
                    .map(player -> {
                        User user = new User(player.getName(), player.getUniqueId());
                        user.setAddress(player.getConnection().getAddress().getHostString());
                        return user;
                    }).collect(Collectors.toCollection(HashSet::new)));
            data.setVersion(Sponge.getPlatform().getMinecraftVersion().getName());
        }
        
        packet.setData(data);
        ServerManager.getInstance().sendResponse(packet);
    }
}