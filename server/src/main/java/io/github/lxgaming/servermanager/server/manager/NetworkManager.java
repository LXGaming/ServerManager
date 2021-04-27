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

package io.github.lxgaming.servermanager.server.manager;

import com.google.common.collect.Lists;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.configuration.Config;
import io.github.lxgaming.servermanager.common.configuration.category.NetworkCategory;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.network.Network;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.network.netty.ServerChannelInitializer;
import io.github.lxgaming.servermanager.server.task.HeartbeatTask;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.internal.SystemPropertyUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class NetworkManager {
    
    public static final List<Connection> CONNECTIONS = Lists.newCopyOnWriteArrayList();
    public static final String NETTY_TRANSPORT_NO_NATIVE = "io.netty.transport.noNative";
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkManager.class);
    
    private static Network network;
    private static SocketAddress socketAddress;
    private static Channel channel;
    
    public static void prepare() {
        NetworkCategory category = Server.getInstance().getConfig().map(Config::getNetworkCategory).orElseThrow(NullPointerException::new);
        if (category.getPort() <= 0 || category.getPort() > 65535) {
            LOGGER.warn("Port is out of bounds. Resetting to {}", NetworkCategory.DEFAULT_PORT);
            category.setPort(NetworkCategory.DEFAULT_PORT);
        }
        
        if (category.isNativeTransport() && SystemPropertyUtil.getBoolean(NETTY_TRANSPORT_NO_NATIVE, false)) {
            LOGGER.warn("NativeTransport is explicitly disabled");
            category.setNativeTransport(false);
        }
        
        if (category.getMaximumThreads() < 0) {
            LOGGER.warn("MaximumThreads is out of bounds. Resetting to {}", NetworkCategory.DEFAULT_MAXIMUM_THREADS);
            category.setMaximumThreads(NetworkCategory.DEFAULT_MAXIMUM_THREADS);
        }
        
        if (category.getReadTimeout() < 0) {
            LOGGER.warn("ReadTimeout is out of bounds. Resetting to {}", NetworkCategory.DEFAULT_READ_TIMEOUT);
            category.setReadTimeout(NetworkCategory.DEFAULT_READ_TIMEOUT);
        }
        
        network = Network.builder()
                .maximumThreads(category.getMaximumThreads())
                .nativeTransport(category.isNativeTransport())
                .build();
        
        socketAddress = new InetSocketAddress(category.getHost(), category.getPort());
        
        LOGGER.info("Server connections will use {} channels", network.getTransportType().getName());
    }
    
    public static void execute() {
        ServerBootstrap serverBootstrap = network.createServerBootstrap();
        serverBootstrap.childHandler(new ServerChannelInitializer());
        serverBootstrap.bind(socketAddress).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                channel = future.channel();
                ServerManagerImpl.getInstance().getTaskManager().schedule(new HeartbeatTask());
                LOGGER.info("Listening on {}", Toolbox.getAddress(socketAddress));
            } else {
                LOGGER.info("Can't bind to {}", Toolbox.getAddress(socketAddress), future.cause());
            }
        });
    }
    
    public static void shutdown(long timeout, @NonNull TimeUnit unit) {
        try {
            for (Connection connection : CONNECTIONS) {
                connection.closeWith(new DisconnectPacket("Proxy shutting down."));
            }
            
            if (channel != null) {
                LOGGER.info("Closing endpoint {}", Toolbox.getAddress(channel.localAddress()));
                channel.close().sync();
            }
            
            network.shutdown(timeout, unit);
            LOGGER.info("Successfully terminated network, continuing with shutdown process...");
        } catch (Exception ex) {
            LOGGER.error("Failed to terminate network, continuing with shutdown process...");
        }
    }
    
    public static void forward(Platform platform, Packet packet, UUID... excludedIds) {
        for (Connection connection : CONNECTIONS) {
            if (connection.isClosed() || connection.getState() != StateRegistry.INSTANCE) {
                continue;
            }
            
            if (connection.getInstance() == null || ArrayUtils.contains(excludedIds, connection.getInstance().getId())) {
                continue;
            }
            
            if (connection.getInstance().getPlatform().equals(platform)) {
                connection.write(packet);
            }
        }
    }
    
    public static void forward(UUID id, Packet packet) {
        Connection connection = getConnection(id);
        if (connection != null) {
            connection.write(packet);
        }
    }
    
    public static Connection getConnection(UUID id) {
        for (Connection connection : CONNECTIONS) {
            if (connection.isClosed() || connection.getState() != StateRegistry.INSTANCE) {
                continue;
            }
            
            if (connection.getInstance().getId().equals(id)) {
                return connection;
            }
        }
        
        return null;
    }
    
    public static Instance getInstance(UUID id) {
        Connection connection = getConnection(id);
        if (connection != null) {
            return connection.getInstance();
        }
        
        return null;
    }
    
    public static Collection<Instance> getInstances() {
        Set<Instance> instances = new HashSet<>();
        for (Connection connection : CONNECTIONS) {
            if (connection.isClosed() || connection.getState() != StateRegistry.INSTANCE) {
                continue;
            }
            
            instances.add(connection.getInstance());
        }
        
        return instances;
    }
}