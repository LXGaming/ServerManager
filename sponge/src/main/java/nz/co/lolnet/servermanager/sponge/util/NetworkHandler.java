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
import nz.co.lolnet.servermanager.common.data.Platform;
import nz.co.lolnet.servermanager.common.data.Server;
import nz.co.lolnet.servermanager.common.data.User;
import nz.co.lolnet.servermanager.common.network.INetworkHandler;
import nz.co.lolnet.servermanager.common.network.packets.CommandPacket;
import nz.co.lolnet.servermanager.common.network.packets.ForwardPacket;
import nz.co.lolnet.servermanager.common.network.packets.StatePacket;
import nz.co.lolnet.servermanager.common.network.packets.StatusPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.sponge.ServerManager;
import nz.co.lolnet.servermanager.sponge.SpongePlugin;
import nz.co.lolnet.servermanager.sponge.interfaces.server.dedicated.IMixinServerHangWatchdog;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.stream.Collectors;

public class NetworkHandler implements INetworkHandler {
    
    @Override
    public void handleCommand(CommandPacket packet) {
        if (Toolbox.isBlank(packet.getCommand())) {
            return;
        }
        
        ServerManager.getInstance().getLogger().info("Processing {} for {}", packet.getCommand(), packet.getUser());
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
        ServerManager.getInstance().getRedisService().publish(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        Server server = new Server();
        server.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        server.setState(Platform.State.valueOf(Sponge.getGame().getState().name()));
        server.setType(Platform.Type.SPONGE);
        
        if (Sponge.isServerAvailable()) {
            server.setTicksPerSecond(Sponge.getServer().getTicksPerSecond());
            server.setUsers(Sponge.getServer().getOnlinePlayers().stream()
                    .map(player -> User.of(player.getName(), player.getUniqueId()))
                    .collect(Collectors.toCollection(HashSet::new)));
            
            server.setVersion(Sponge.getPlatform().getMinecraftVersion().getName());
        }
        
        ServerManager.getInstance().getRedisService().publish(packet);
    }
}