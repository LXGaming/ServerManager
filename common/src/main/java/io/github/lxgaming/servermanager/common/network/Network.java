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

package io.github.lxgaming.servermanager.common.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;

import java.util.concurrent.TimeUnit;

public class Network {
    
    private final TransportType transportType;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    
    private Network(TransportType transportType, int maximumThreads) {
        this.transportType = transportType;
        this.bossGroup = transportType.createEventLoopGroup(maximumThreads, TransportType.Type.BOSS);
        this.workerGroup = transportType.createEventLoopGroup(maximumThreads, TransportType.Type.WORKER);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(transportType.getSocketChannelClass());
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
    
    public ServerBootstrap createServerBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(transportType.getServerSocketChannelClass());
        return serverBootstrap;
    }
    
    public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        bossGroup.shutdownGracefully();
        if (!bossGroup.awaitTermination(timeout, unit)) {
            throw new InterruptedException();
        }
        
        workerGroup.shutdownGracefully();
        if (!workerGroup.awaitTermination(timeout, unit)) {
            throw new InterruptedException();
        }
    }
    
    public TransportType getTransportType() {
        return transportType;
    }
    
    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }
    
    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }
    
    public static class Builder {
        
        private int maximumThreads;
        private boolean nativeTransport;
        
        public Network build() {
            if (nativeTransport) {
                if (Epoll.isAvailable()) {
                    return new Network(TransportType.EPOLL, maximumThreads);
                }
                
                if (KQueue.isAvailable()) {
                    return new Network(TransportType.KQUEUE, maximumThreads);
                }
            }
            
            return new Network(TransportType.NIO, maximumThreads);
        }
        
        public int getMaximumThreads() {
            return maximumThreads;
        }
        
        public Builder maximumThreads(int maximumThreads) {
            this.maximumThreads = maximumThreads;
            return this;
        }
        
        public boolean isNativeTransport() {
            return nativeTransport;
        }
        
        public Builder nativeTransport(boolean nativeTransport) {
            this.nativeTransport = nativeTransport;
            return this;
        }
    }
}