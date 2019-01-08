/*
 * Copyright 2019 lolnet.co.nz
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

package nz.co.lolnet.servermanager.sponge.listener;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.packet.UserPacket;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class SpongeListener {
    
    @Listener(order = Order.LATE)
    public void onClientConnectionJoin(ClientConnectionEvent.Join event) {
        ServerManager.getInstance().sendResponse(new UserPacket.Add(new User(event.getTargetEntity().getName(), event.getTargetEntity().getUniqueId())));
    }
    
    @Listener(order = Order.LATE)
    public void onClientConnectionDisconnect(ClientConnectionEvent.Disconnect event) {
        ServerManager.getInstance().sendResponse(new UserPacket.Remove(new User(event.getTargetEntity().getName(), event.getTargetEntity().getUniqueId())));
    }
}