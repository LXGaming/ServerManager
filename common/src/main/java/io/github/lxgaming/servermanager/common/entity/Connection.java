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

package io.github.lxgaming.servermanager.common.entity;

import com.google.common.base.Preconditions;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.netty.PacketDecoder;
import io.github.lxgaming.servermanager.common.network.netty.PacketEncoder;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

public class Connection extends ChannelInboundHandlerAdapter {
    
    public static final String NAME = "handler";
    protected static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);
    
    protected final Channel channel;
    protected InetSocketAddress address;
    protected StateRegistry state;
    protected SessionHandler sessionHandler;
    protected Instance instance;
    protected Set<String> intents;
    protected boolean knownDisconnect;
    
    protected Connection(Channel channel) {
        this.channel = channel;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (sessionHandler != null) {
            sessionHandler.connected();
        }
        
        if (address != null) {
            LOGGER.info("{} has connected", Toolbox.getAddress(address));
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (sessionHandler != null) {
            sessionHandler.disconnected();
        }
        
        if (address != null && !knownDisconnect) {
            LOGGER.info("{} has disconnected", Toolbox.getAddress(address));
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (sessionHandler == null) {
                return;
            }
            
            if (sessionHandler.beforeHandle()) {
                return;
            }
            
            if (isClosed()) {
                return;
            }
            
            if (msg instanceof Packet) {
                Packet packet = (Packet) msg;
                if (!packet.handle(sessionHandler)) {
                    sessionHandler.handleGeneric(packet);
                }
            } else if (msg instanceof HAProxyMessage) {
                HAProxyMessage proxyMessage = (HAProxyMessage) msg;
                this.address = new InetSocketAddress(proxyMessage.sourceAddress(), proxyMessage.sourcePort());
            } else if (msg instanceof ByteBuf) {
                sessionHandler.handleUnknown((ByteBuf) msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            if (sessionHandler != null) {
                try {
                    sessionHandler.exception(cause);
                } catch (Exception ex) {
                    LOGGER.error("{}: exception handling exception", Toolbox.getAddress(address), cause);
                }
            }
            
            if (cause instanceof ReadTimeoutException) {
                LOGGER.error("{}: read timed out", Toolbox.getAddress(address));
            } else {
                LOGGER.error("{}: exception encountered", Toolbox.getAddress(address), cause);
            }
            
            ctx.close();
        }
    }
    
    protected void ensureInEventLoop() {
        Preconditions.checkState(channel.eventLoop().inEventLoop(), "Not in event loop");
    }
    
    protected void ensureOpen() {
        Preconditions.checkState(!isClosed(), "Connection is closed");
    }
    
    public boolean isAutoReading() {
        return channel.config().isAutoRead();
    }
    
    public void setAutoReading(boolean autoReading) {
        ensureInEventLoop();
        
        channel.config().setAutoRead(autoReading);
        if (autoReading) {
            channel.read();
        }
    }
    
    public void write(Object msg) {
        if (channel.isActive()) {
            channel.writeAndFlush(msg, channel.voidPromise());
        } else {
            ReferenceCountUtil.release(msg);
        }
    }
    
    public void delayedWrite(Object msg) {
        if (channel.isActive()) {
            channel.write(msg, channel.voidPromise());
        } else {
            ReferenceCountUtil.release(msg);
        }
    }
    
    public void flush() {
        if (channel.isActive()) {
            channel.flush();
        }
    }
    
    public boolean isClosed() {
        return !channel.isActive();
    }
    
    public void close() {
        if (channel.isActive()) {
            if (channel.eventLoop().inEventLoop()) {
                knownDisconnect = true;
                channel.close();
            } else {
                channel.eventLoop().execute(this::close);
            }
        }
    }
    
    public void closeWith(Object msg) {
        if (channel.isActive()) {
            knownDisconnect = true;
            channel.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    public boolean hasIntent(String key) {
        if (intents == null) {
            return false;
        }
        
        for (String intent : intents) {
            if (key.matches(intent)) {
                return true;
            }
        }
        
        return false;
    }
    
    public Channel getChannel() {
        return channel;
    }
    
    public InetSocketAddress getAddress() {
        return address;
    }
    
    public StateRegistry getState() {
        return state;
    }
    
    public void setState(StateRegistry state) {
        ensureInEventLoop();
        
        this.state = state;
        this.channel.pipeline().get(PacketDecoder.class).setState(state);
        this.channel.pipeline().get(PacketEncoder.class).setState(state);
    }
    
    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
    
    public void setSessionHandler(SessionHandler sessionHandler) {
        ensureInEventLoop();
        
        if (this.sessionHandler != null) {
            this.sessionHandler.deactivated();
        }
        
        this.sessionHandler = sessionHandler;
        sessionHandler.activated();
    }
    
    public Instance getInstance() {
        return instance;
    }
    
    public void setInstance(Instance instance) {
        ensureInEventLoop();
        
        this.instance = instance;
    }
    
    public Set<String> getIntents() {
        return intents;
    }
    
    public void setIntents(Set<String> intents) {
        this.intents = intents;
    }
    
    public boolean isKnownDisconnect() {
        return knownDisconnect;
    }
}