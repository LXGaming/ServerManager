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

package nz.co.lolnet.servermanager.sponge.util;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerHangWatchdog;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.ServerInfo;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.AbstractNetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.sponge.ServerManagerImpl;
import nz.co.lolnet.servermanager.sponge.SpongePlugin;
import nz.co.lolnet.servermanager.sponge.interfaces.server.dedicated.IMixinServerHangWatchdog;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

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
    public void handlePing(PingPacket packet) {
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void handleState(StatePacket packet) {
        packet.setState(SpongePlugin.getInstance().getState());
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        serverInfo.setState(SpongePlugin.getInstance().getState());
        serverInfo.setType(ServerManager.getInstance().getPlatformType());
        
        if (Sponge.isServerAvailable()) {
            serverInfo.setTicksPerSecond(Sponge.getServer().getTicksPerSecond());
            serverInfo.setUsers(Sponge.getServer().getOnlinePlayers().stream()
                    .map(player -> new User(player.getName(), player.getUniqueId()))
                    .collect(Collectors.toCollection(HashSet::new)));
            
            serverInfo.setVersion(Sponge.getPlatform().getMinecraftVersion().getName());
        }
        
        packet.setServerInfo(serverInfo);
        ServerManager.getInstance().sendResponse(packet);
    }
}