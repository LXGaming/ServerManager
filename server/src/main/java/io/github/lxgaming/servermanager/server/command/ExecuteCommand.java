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

import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.data.User;
import io.github.lxgaming.servermanager.api.network.packet.CommandPacket;
import io.github.lxgaming.servermanager.api.util.Reference;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.data.Connection;
import io.github.lxgaming.servermanager.server.manager.ConnectionManager;

import java.util.List;

public class ExecuteCommand extends AbstractCommand {
    
    public ExecuteCommand() {
        addAlias("execute");
        setDescription("Executes commands on the targeted server");
        setUsage("<Server> <Command>");
    }
    
    @Override
    public void execute(List<String> arguments) {
        if (arguments.size() < 1) {
            ServerManager.getInstance().getLogger().error("Not enough arguments");
            return;
        }
        
        Connection connection = ConnectionManager.getConnection(arguments.remove(0)).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().error("Failed to find connection");
            return;
        }
        
        String command = String.join(" ", arguments);
        if (Toolbox.isBlank(command)) {
            ServerManager.getInstance().getLogger().error("Cannot send blank command");
            return;
        }
        
        ServerManagerImpl.getInstance().sendRequest(connection.getId(), new CommandPacket(command, new User(Reference.NAME, Platform.CONSOLE_UUID)));
        ServerManager.getInstance().getLogger().info("Execute sent");
    }
}