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
import nz.co.lolnet.servermanager.api.data.Platform;
import nz.co.lolnet.servermanager.api.data.ServerInfo;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ForwardPacket;
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

public class NetworkHandlerImpl implements NetworkHandler {
    
    @Override
    public void handleCommand(CommandPacket packet) {
        if (Toolbox.isBlank(packet.getCommand())) {
            return;
        }
        
        ServerManagerImpl.getInstance().getLogger().info("Processing {} for {}", packet.getCommand(), packet.getUser());
        if (packet.getCommand().equals("servermanager:terminate")) {
            ((IMixinServerHangWatchdog) new ServerHangWatchdog((DedicatedServer) Sponge.getServer())).scheduleHalt();
            return;
        }
        
        Task.builder().execute(() -> {
            Sponge.getCommandManager().process(new SpongeCommandSource(), packet.getCommand());
        }).submit(SpongePlugin.getInstance().getPluginContainer());
    }
    
    @Override
    public void handleForward(ForwardPacket packet) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public void handleState(StatePacket packet) {
        packet.setState(Platform.State.valueOf(Sponge.getGame().getState().name()));
        ServerManager.getInstance().sendPacket(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        serverInfo.setState(Platform.State.valueOf(Sponge.getGame().getState().name()));
        serverInfo.setType(Platform.Type.SPONGE);
        
        if (Sponge.isServerAvailable()) {
            serverInfo.setTicksPerSecond(Sponge.getServer().getTicksPerSecond());
            serverInfo.setUsers(Sponge.getServer().getOnlinePlayers().stream()
                    .map(player -> User.of(player.getName(), player.getUniqueId()))
                    .collect(Collectors.toCollection(HashSet::new)));
            
            serverInfo.setVersion(Sponge.getPlatform().getMinecraftVersion().getName());
        }
        
        ServerManager.getInstance().sendPacket(packet);
    }
}