/*
 * Copyright 2018 Alex Thomson
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

package io.github.lxgaming.servermanager.server.command;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.network.packet.PingPacket;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.data.Connection;
import io.github.lxgaming.servermanager.server.manager.ConnectionManager;

import java.util.List;

public class PingCommand extends AbstractCommand {
    
    public PingCommand() {
        addAlias("ping");
        setDescription("Ping targeted server");
    }
    
    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            ServerManager.getInstance().getLogger().error("Not enough arguments");
            return;
        }
        
        Connection connection = ConnectionManager.getConnection(arguments.remove(0)).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().error("Failed to find connection");
            return;
        }
        
        ServerManagerImpl.getInstance().sendRequest(connection.getId(), new PingPacket(System.currentTimeMillis()));
        ServerManager.getInstance().getLogger().info("Ping sent");
    }
}