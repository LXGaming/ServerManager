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

package io.github.lxgaming.servermanager.client.task;

import io.github.lxgaming.servermanager.client.Client;
import io.github.lxgaming.servermanager.client.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.client.configuration.category.NetworkCategoryImpl;
import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.task.Task;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

public class ReconnectTask extends Task {
    
    private final Bootstrap bootstrap;
    private final GenericFutureListener<ChannelFuture> listener;
    
    public ReconnectTask(Bootstrap bootstrap, GenericFutureListener<ChannelFuture> listener) {
        this.bootstrap = bootstrap;
        this.listener = listener;
    }
    
    @Override
    public boolean prepare() {
        type(Type.DEFAULT);
        return true;
    }
    
    @Override
    public void execute() {
        int maximumReconnectDelay = Client.getInstance().getConfig()
                .map(ConfigImpl::getNetworkCategory)
                .map(NetworkCategoryImpl::getMaximumReconnectDelay)
                .filter(value -> value >= NetworkCategoryImpl.DEFAULT_MINIMUM_RECONNECT_DELAY)
                .orElse(NetworkCategoryImpl.DEFAULT_MAXIMUM_RECONNECT_DELAY);
        
        if (getDelay() <= 0) {
            delay(2000L, TimeUnit.MILLISECONDS);
        }
        
        ChannelFuture channelFuture = bootstrap.connect();
        channelFuture.addListener(listener);
        channelFuture.addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                delay(0L, TimeUnit.MILLISECONDS);
                future.channel().closeFuture().addListener((ChannelFuture closeFuture) -> {
                    Connection connection = Client.getInstance().getConnection();
                    if (closeFuture.isSuccess() && (connection == null || connection.getState() != StateRegistry.INSTANCE)) {
                        return;
                    }
                    
                    ServerManagerImpl.getInstance().getTaskManager().schedule(this);
                });
            } else {
                delay(Math.min(getDelay() << 1, maximumReconnectDelay), TimeUnit.MILLISECONDS);
                LOGGER.warn("Attempting to reconnect in {}", Toolbox.getDuration(getDelay(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS));
                ServerManagerImpl.getInstance().getTaskManager().schedule(this);
            }
        });
    }
}