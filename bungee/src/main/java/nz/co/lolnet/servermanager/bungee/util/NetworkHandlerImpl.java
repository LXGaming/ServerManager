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
import nz.co.lolnet.servermanager.api.data.Platform;
import nz.co.lolnet.servermanager.api.data.ServerInfo;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ForwardPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.bungee.BungeePlugin;
import nz.co.lolnet.servermanager.bungee.ServerManagerImpl;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;

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
        ServerManagerImpl.getInstance().getRedisService().publish(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        serverInfo.setState(Platform.State.SERVER_STARTED);
        serverInfo.setType(Platform.Type.BUNGEE);
        serverInfo.setUsers(ProxyServer.getInstance().getPlayers().stream()
                .map(proxiedPlayer -> User.of(proxiedPlayer.getName(), proxiedPlayer.getUniqueId()))
                .collect(Collectors.toCollection(HashSet::new)));
        serverInfo.setVersion(BungeePlugin.getVersion());
        ServerManagerImpl.getInstance().getRedisService().publish(packet);
    }
}