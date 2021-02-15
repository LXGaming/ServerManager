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

package io.github.lxgaming.servermanager.server.network.session;

import io.github.lxgaming.servermanager.common.manager.InstanceManager;
import io.github.lxgaming.servermanager.common.network.SessionHandler;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.entity.ConnectionImpl;

public class ApplicationSessionHandler implements SessionHandler {
    
    private final ConnectionImpl connection;
    
    public ApplicationSessionHandler(ConnectionImpl connection) {
        this.connection = connection;
    }
    
    @Override
    public void activated() {
        ServerManagerImpl.getInstance().getLogger().info("ApplicationSessionHandler active");
        connection.setState(StateRegistry.APPLICATION);
    }
    
    @Override
    public boolean handle(HeartbeatPacket packet) {
        if (connection.isHeartbeatPending() && connection.getHeartbeatTime() == packet.getValue()) {
            connection.setHeartbeatPending(false);
            connection.setLatency(System.currentTimeMillis() - connection.getHeartbeatTime());
        } else {
            connection.disconnect("Timed out");
        }
        
        return true;
    }
    
    @Override
    public boolean handle(ListPacket.Request packet) {
        connection.write(new ListPacket.Response(InstanceManager.INSTANCES));
        return true;
    }
}