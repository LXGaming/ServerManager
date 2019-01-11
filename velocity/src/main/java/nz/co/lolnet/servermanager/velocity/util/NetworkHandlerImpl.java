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

package nz.co.lolnet.servermanager.velocity.util;

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.Implementation;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.AbstractNetworkHandler;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.velocity.ServerManagerImpl;
import nz.co.lolnet.servermanager.velocity.VelocityPlugin;

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
        
        VelocityPlugin.getInstance().getProxy().getCommandManager().execute(new VelocityCommandSource(), packet.getCommand());
    }
    
    @Override
    public void handlePing(PingPacket packet) {
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void handleState(StatePacket packet) {
        packet.setState(Platform.State.SERVER_STARTED);
        ServerManager.getInstance().sendResponse(packet);
    }
    
    @Override
    public void handleStatus(StatusPacket packet) {
        Implementation.Data data = new Implementation.Data();
        data.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        data.setState(Platform.State.SERVER_STARTED);
        data.setUsers(VelocityPlugin.getInstance().getProxy().getAllPlayers().stream()
                .map(player -> {
                    User user = new User(player.getUsername(), player.getUniqueId());
                    user.setAddress(player.getRemoteAddress().getHostString());
                    return user;
                }).collect(Collectors.toCollection(HashSet::new)));
        data.setVersion(VelocityPlugin.getVersion());
        
        packet.setData(data);
        ServerManager.getInstance().sendResponse(packet);
    }
}