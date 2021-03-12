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

package io.github.lxgaming.servermanager.client.manager;

import io.github.lxgaming.servermanager.client.Client;
import io.github.lxgaming.servermanager.client.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.client.configuration.category.NetworkCategoryImpl;
import io.github.lxgaming.servermanager.client.network.netty.ClientChannelInitializer;
import io.github.lxgaming.servermanager.client.task.ReconnectTask;
import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.configuration.category.NetworkCategory;
import io.github.lxgaming.servermanager.common.network.Network;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public final class NetworkManager {
    
    public static final String NETTY_TRANSPORT_NO_NATIVE = "io.netty.transport.noNative";
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkManager.class);
    
    private static Network network;
    private static SocketAddress socketAddress;
    private static Channel channel;
    
    public static void prepare() {
        NetworkCategoryImpl category = Client.getInstance().getConfig().map(ConfigImpl::getNetworkCategory).orElseThrow(NullPointerException::new);
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
        
        if (category.getMaximumReconnectDelay() < NetworkCategoryImpl.DEFAULT_MINIMUM_RECONNECT_DELAY) {
            LOGGER.warn("MaximumReconnectDelay is out of bounds. Resetting to {}", NetworkCategoryImpl.DEFAULT_MAXIMUM_RECONNECT_DELAY);
            category.setMaximumReconnectDelay(NetworkCategoryImpl.DEFAULT_MAXIMUM_RECONNECT_DELAY);
        }
        
        network = Network.builder()
                .maximumThreads(category.getMaximumThreads())
                .nativeTransport(category.isNativeTransport())
                .build();
        
        socketAddress = new InetSocketAddress(category.getHost(), category.getPort());
        LOGGER.info("Client connections will use {} channels", network.getTransportType().getName());
    }
    
    public static void execute() {
        Bootstrap bootstrap = network.createBootstrap();
        bootstrap.handler(new ClientChannelInitializer());
        bootstrap.remoteAddress(socketAddress);
        
        ChannelFutureListener listener = future -> {
            if (future.isSuccess()) {
                channel = future.channel();
                LOGGER.info("Connected to {}", Toolbox.getAddress(socketAddress));
            } else {
                LOGGER.warn(future.cause().getMessage());
            }
        };
        
        if (Client.getInstance().getConfig().map(ConfigImpl::getNetworkCategory).map(NetworkCategoryImpl::isReconnect).orElse(false)) {
            ServerManagerImpl.getInstance().getTaskManager().schedule(new ReconnectTask(bootstrap, listener));
        } else {
            bootstrap.connect().addListener(listener);
        }
    }
    
    public static void shutdown() {
        try {
            LOGGER.info("Closing endpoint {}", Toolbox.getAddress(channel.localAddress()));
            channel.close().sync();
            network.shutdown(5L, TimeUnit.SECONDS);
            LOGGER.info("Successfully terminated Netty, continuing with shutdown process...");
        } catch (Exception ex) {
            LOGGER.error("Failed to terminate Netty, continuing with shutdown process...");
        }
    }
}