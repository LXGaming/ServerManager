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

package nz.co.lolnet.servermanager.bungee.util;

import net.md_5.bungee.api.ProxyServer;
import nz.co.lolnet.servermanager.bungee.BungeePlugin;
import nz.co.lolnet.servermanager.bungee.ServerManager;
import nz.co.lolnet.servermanager.common.data.Platform;
import nz.co.lolnet.servermanager.common.data.Server;
import nz.co.lolnet.servermanager.common.data.User;
import nz.co.lolnet.servermanager.common.managers.ServiceManager;
import nz.co.lolnet.servermanager.common.network.INetworkHandler;
import nz.co.lolnet.servermanager.common.network.packets.CommandPacket;
import nz.co.lolnet.servermanager.common.network.packets.ForwardPacket;
import nz.co.lolnet.servermanager.common.network.packets.StatePacket;
import nz.co.lolnet.servermanager.common.network.packets.StatusPacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;

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
            ServiceManager.schedule(() -> Runtime.getRuntime().halt(1), 30000L, 0L, false);
            Runtime.getRuntime().exit(1);
            return;
        }
        
        ProxyServer.getInstance().getPluginManager().dispatchCommand(new BungeeCommandSender(), packet.getCommand());
    }
    
    @Override
    public void handleForward(ForwardPacket packet) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public void handleState(StatePacket packet) {
        packet.setState(Platform.State.SERVER_STARTED);
        ServerManager.getInstance().getRedisService().publish(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        Server server = new Server();
        server.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        server.setState(Platform.State.SERVER_STARTED);
        server.setType(Platform.Type.BUNGEE);
        server.setUsers(ProxyServer.getInstance().getPlayers().stream()
                .map(proxiedPlayer -> User.of(proxiedPlayer.getName(), proxiedPlayer.getUniqueId()))
                .collect(Collectors.toCollection(HashSet::new)));
        server.setVersion(BungeePlugin.getVersion());
        ServerManager.getInstance().getRedisService().publish(packet);
    }
}