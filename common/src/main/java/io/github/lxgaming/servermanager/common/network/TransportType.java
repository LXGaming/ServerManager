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

import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

public enum TransportType {
    
    NIO("NIO", NioSocketChannel.class, NioServerSocketChannel.class) {
        @Override
        public EventLoopGroup createEventLoopGroup(int maximumThreads, Type type) {
            return new NioEventLoopGroup(maximumThreads, createThreadFactory(getName(), type));
        }
    },
    
    EPOLL("Epoll", EpollSocketChannel.class, EpollServerSocketChannel.class) {
        @Override
        public EventLoopGroup createEventLoopGroup(int maximumThreads, Type type) {
            return new EpollEventLoopGroup(maximumThreads, createThreadFactory(getName(), type));
        }
    },
    
    KQUEUE("KQueue", KQueueSocketChannel.class, KQueueServerSocketChannel.class) {
        @Override
        public EventLoopGroup createEventLoopGroup(int maximumThreads, Type type) {
            return new KQueueEventLoopGroup(maximumThreads, createThreadFactory(getName(), type));
        }
    };
    
    private final String name;
    private final Class<? extends SocketChannel> socketChannelClass;
    private final Class<? extends ServerSocketChannel> serverSocketChannelClass;
    
    TransportType(String name, Class<? extends SocketChannel> socketChannelClass, Class<? extends ServerSocketChannel> serverSocketChannelClass) {
        this.name = name;
        this.socketChannelClass = socketChannelClass;
        this.serverSocketChannelClass = serverSocketChannelClass;
    }
    
    public abstract EventLoopGroup createEventLoopGroup(int maximumThreads, Type type);
    
    private static ThreadFactory createThreadFactory(String name, Type type) {
        return Toolbox.newThreadFactory("Netty " + name + ' ' + type.getName() + " #%d");
    }
    
    public String getName() {
        return name;
    }
    
    public Class<? extends SocketChannel> getSocketChannelClass() {
        return socketChannelClass;
    }
    
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return serverSocketChannelClass;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public enum Type {
        
        BOSS("Boss"),
        WORKER("Worker");
        
        private final String name;
        
        Type(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}