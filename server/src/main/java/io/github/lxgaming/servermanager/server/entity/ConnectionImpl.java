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

package io.github.lxgaming.servermanager.server.entity;

import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class ConnectionImpl extends Connection {
    
    // Heartbeat
    private volatile boolean heartbeatPending;
    private volatile long heartbeatTime;
    
    private volatile long latency;
    
    public ConnectionImpl(Channel channel) {
        super(channel);
        this.state = StateRegistry.HANDSHAKE;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.address = (InetSocketAddress) channel.remoteAddress();
        super.channelActive(ctx);
        NetworkManager.CONNECTIONS.add(this);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        NetworkManager.CONNECTIONS.remove(this);
    }
    
    public void disconnect(String message) {
        ServerManagerImpl.getInstance().getLogger().info("{} has disconnected: {}", Toolbox.getAddress(getAddress()), message);
        closeWith(new DisconnectPacket(message));
    }
    
    public boolean isHeartbeatPending() {
        return heartbeatPending;
    }
    
    public void setHeartbeatPending(boolean heartbeatPending) {
        ensureInEventLoop();
        
        this.heartbeatPending = heartbeatPending;
    }
    
    public long getHeartbeatTime() {
        return heartbeatTime;
    }
    
    public void setHeartbeatTime(long heartbeatTime) {
        ensureInEventLoop();
        
        this.heartbeatTime = heartbeatTime;
    }
    
    public long getLatency() {
        return latency;
    }
    
    public void setLatency(long latency) {
        ensureInEventLoop();
        
        this.latency = latency;
    }
}