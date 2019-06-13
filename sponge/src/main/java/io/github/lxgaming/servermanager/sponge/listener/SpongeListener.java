/*
 * Copyright 2019 Alex Thomson
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

package io.github.lxgaming.servermanager.sponge.listener;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.data.User;
import io.github.lxgaming.servermanager.api.network.packet.UserPacket;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class SpongeListener {
    
    @Listener(order = Order.LATE)
    public void onClientConnectionJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        User user = new User(player.getName(), player.getUniqueId());
        user.setAddress(player.getConnection().getAddress().getHostString());
        ServerManager.getInstance().sendResponse(new UserPacket.Add(user));
    }
    
    @Listener(order = Order.LATE)
    public void onClientConnectionDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        User user = new User(player.getName(), player.getUniqueId());
        user.setAddress(player.getConnection().getAddress().getHostString());
        ServerManager.getInstance().sendResponse(new UserPacket.Remove(user));
    }
}