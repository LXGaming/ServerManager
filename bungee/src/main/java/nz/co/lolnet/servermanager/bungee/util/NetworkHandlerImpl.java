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
import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.ServerInfo;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.AbstractNetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.bungee.BungeePlugin;
import nz.co.lolnet.servermanager.bungee.ServerManagerImpl;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.stream.Collectors;

public class NetworkHandlerImpl extends AbstractNetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        return Toolbox.isBlank(packet.getForwardTo()) && packet.getType().equals(Packet.Type.REQUEST);
    }
    
    @Override
    public void handleCommand(CommandPacket packet) {
        if (Toolbox.isBlank(packet.getCommand())) {
            return;
        }
        
        ServerManagerImpl.getInstance().getLogger().info("Processing {} for {}", packet.getCommand(), packet.getUser());
        if (packet.getCommand().equals("servermanager:terminate")) {
            ServiceManager.schedule(() -> Runtime.getRuntime().halt(1), 30000L, 0L);
            Runtime.getRuntime().exit(1);
            return;
        }
        
        ProxyServer.getInstance().getPluginManager().dispatchCommand(new BungeeCommandSender(), packet.getCommand());
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        packet.setType(Packet.Type.RESPONSE);
        ServerManager.getInstance().sendPacket(packet);
    }
    
    @Override
    public void handleState(StatePacket packet) {
        packet.setType(Packet.Type.RESPONSE);
        packet.setState(Platform.State.SERVER_STARTED);
        ServerManager.getInstance().sendPacket(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        serverInfo.setState(Platform.State.SERVER_STARTED);
        serverInfo.setType(Platform.Type.BUNGEECORD);
        serverInfo.setUsers(ProxyServer.getInstance().getPlayers().stream()
                .map(player -> new User(player.getName(), player.getUniqueId()))
                .collect(Collectors.toCollection(HashSet::new)));
        serverInfo.setVersion(BungeePlugin.getVersion());
        
        packet.setType(Packet.Type.RESPONSE);
        packet.setServerInfo(serverInfo);
        ServerManager.getInstance().sendPacket(packet);
    }
}