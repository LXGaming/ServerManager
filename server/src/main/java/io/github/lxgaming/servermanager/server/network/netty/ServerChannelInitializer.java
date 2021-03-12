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

package io.github.lxgaming.servermanager.server.network.netty;

import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.network.Direction;
import io.github.lxgaming.servermanager.common.network.netty.PacketDecoder;
import io.github.lxgaming.servermanager.common.network.netty.PacketEncoder;
import io.github.lxgaming.servermanager.common.network.netty.VarintFrameDecoder;
import io.github.lxgaming.servermanager.common.network.netty.VarintFrameEncoder;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;
import io.github.lxgaming.servermanager.server.network.session.HandshakeSessionHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ServerChannelInitializer extends ChannelInitializer<Channel> {
    
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(VarintFrameDecoder.NAME, new VarintFrameDecoder());
        ch.pipeline().addLast(VarintFrameEncoder.NAME, VarintFrameEncoder.INSTANCE);
        
        ch.pipeline().addLast(PacketDecoder.NAME, new PacketDecoder(Direction.SERVERBOUND));
        ch.pipeline().addLast(PacketEncoder.NAME, new PacketEncoder(Direction.CLIENTBOUND));
        
        ConnectionImpl connection = new ConnectionImpl(ch);
        connection.setSessionHandler(new HandshakeSessionHandler(connection));
        ch.pipeline().addLast(Connection.NAME, connection);
        
        Server.getInstance().getConfig().map(ConfigImpl::getNetworkCategory).ifPresent(category -> {
            ch.pipeline().addFirst(new ReadTimeoutHandler(category.getReadTimeout(), TimeUnit.MILLISECONDS));
            if (category.isProxyProtocol()) {
                ch.pipeline().addFirst(new HAProxyMessageDecoder());
            }
        });
    }
}