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

package io.github.lxgaming.servermanager.bungee.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.data.Implementation;
import io.github.lxgaming.servermanager.api.data.User;
import io.github.lxgaming.servermanager.api.network.AbstractNetworkHandler;
import io.github.lxgaming.servermanager.api.network.Packet;
import io.github.lxgaming.servermanager.api.network.packet.CommandPacket;
import io.github.lxgaming.servermanager.api.network.packet.MessagePacket;
import io.github.lxgaming.servermanager.api.network.packet.PingPacket;
import io.github.lxgaming.servermanager.api.network.packet.StatePacket;
import io.github.lxgaming.servermanager.api.network.packet.StatusPacket;
import io.github.lxgaming.servermanager.bungee.BungeePlugin;
import io.github.lxgaming.servermanager.bungee.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.manager.ServiceManager;
import io.github.lxgaming.servermanager.common.util.Toolbox;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
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
    public void handleMessage(MessagePacket packet) {
        if (Toolbox.isBlank(packet.getMessage())) {
            return;
        }
        
        ChatMessageType chatMessageType = BungeeToolbox.getMessagePosition(packet.getPosition());
        TextComponent textComponent = new TextComponent(BungeeToolbox.deserializeLegacy(packet.getMessage()));
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (Toolbox.isBlank(packet.getPermission()) || player.hasPermission(packet.getPermission())) {
                player.sendMessage(chatMessageType, textComponent);
            }
        }
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
        data.setUsers(ProxyServer.getInstance().getPlayers().stream()
                .map(player -> {
                    User user = new User(player.getName(), player.getUniqueId());
                    if (player.getSocketAddress() instanceof InetSocketAddress) {
                        user.setAddress(((InetSocketAddress) player.getSocketAddress()).getHostString());
                    }
                    
                    return user;
                }).collect(Collectors.toCollection(HashSet::new)));
        data.setVersion(BungeePlugin.getVersion());
        
        packet.setData(data);
        ServerManager.getInstance().sendResponse(packet);
    }
}