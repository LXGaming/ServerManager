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

package nz.co.lolnet.servermanager.velocity.listener;

import com.google.gson.JsonObject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import nz.co.lolnet.redisvelocity.api.event.RedisMessageEvent;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.packet.UserPacket;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.velocity.ServerManagerImpl;

public class VelocityListener {
    
    @Subscribe(order = PostOrder.LATE)
    public void onRedisMessage(RedisMessageEvent event) {
        if (ServerManagerImpl.getInstance().getRedisService().getChannels().contains(event.getChannel())) {
            Toolbox.parseJson(event.getMessage(), JsonObject.class).ifPresent(PacketManager::process);
        }
    }
    
    @Subscribe(order = PostOrder.LATE)
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        User user = new User(player.getUsername(), player.getUniqueId());
        user.setAddress(player.getRemoteAddress().getHostString());
        ServerManager.getInstance().sendResponse(new UserPacket.Add(user));
    }
    
    @Subscribe(order = PostOrder.LATE)
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        User user = new User(player.getUsername(), player.getUniqueId());
        user.setAddress(player.getRemoteAddress().getHostString());
        ServerManager.getInstance().sendResponse(new UserPacket.Remove(user));
    }
}