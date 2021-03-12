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

package io.github.lxgaming.servermanager.server.task;

import io.github.lxgaming.servermanager.common.configuration.category.NetworkCategory;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.task.Task;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;

import java.util.concurrent.TimeUnit;

public class HeartbeatTask extends Task {
    
    @Override
    public boolean prepare() {
        interval(1L, TimeUnit.SECONDS);
        type(Type.FIXED_DELAY);
        return true;
    }
    
    @Override
    public void execute() throws Exception {
        int readTimeout = Server.getInstance().getConfig()
                .map(ConfigImpl::getNetworkCategory)
                .map(NetworkCategory::getReadTimeout)
                .orElse(NetworkCategory.DEFAULT_READ_TIMEOUT) / 2;
        long currentTime = System.currentTimeMillis();
        long heartbeatTime = currentTime - readTimeout;
        for (Connection connection : NetworkManager.CONNECTIONS) {
            if (!(connection instanceof ConnectionImpl)) {
                continue;
            }
            
            ConnectionImpl connectionImpl = (ConnectionImpl) connection;
            if (connectionImpl.getState() != StateRegistry.INSTANCE) {
                continue;
            }
            
            if (connectionImpl.getHeartbeatTime() >= heartbeatTime) {
                continue;
            }
            
            if (connectionImpl.isHeartbeatPending()) {
                connectionImpl.disconnect("Timed out");
                continue;
            }
            
            connectionImpl.getChannel().eventLoop().execute(() -> {
                connectionImpl.setHeartbeatPending(true);
                connectionImpl.setHeartbeatTime(currentTime);
                connectionImpl.write(new HeartbeatPacket(currentTime));
            });
        }
    }
}