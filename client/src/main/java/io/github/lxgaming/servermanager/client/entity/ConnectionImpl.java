/*
 * Copyright 2021 Alex Thomson
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

package io.github.lxgaming.servermanager.client.entity;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.client.Client;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.event.network.ConnectionEventImpl;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class ConnectionImpl extends Connection {
    
    public ConnectionImpl(Channel channel) {
        super(channel);
        this.state = StateRegistry.HANDSHAKE;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.address = (InetSocketAddress) channel.localAddress();
        super.channelActive(ctx);
        Client.getInstance().setConnection(this);
        ServerManager.getInstance().getEventManager().fire(new ConnectionEventImpl.Connect(Platform.CLIENT, this)).join();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Client.getInstance().setConnection(null);
        ServerManager.getInstance().getEventManager().fire(new ConnectionEventImpl.Disconnect(Platform.CLIENT, this)).join();
    }
    
    @Override
    public void setSessionHandler(SessionHandler sessionHandler) {
        LOGGER.debug("{} -> {}", Toolbox.getClassSimpleName(this.sessionHandler), Toolbox.getClassSimpleName(sessionHandler));
        super.setSessionHandler(sessionHandler);
    }
}