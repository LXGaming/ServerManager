/*
 * Copyright 2019 lolnet.co.nz
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

package nz.co.lolnet.servermanager.server.command.connection;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.server.command.AbstractCommand;
import nz.co.lolnet.servermanager.server.data.Connection;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

import java.util.List;

public class UserConnectionCommand extends AbstractCommand {
    
    public UserConnectionCommand() {
        addAlias("user");
        addAlias("users");
        setUsage("<Connection>");
    }
    
    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            ServerManager.getInstance().getLogger().error("Invalid arguments: {}", getUsage());
            return;
        }
        
        Connection connection = ConnectionManager.getConnection(arguments.remove(0)).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().error("Connection doesn't exist");
            return;
        }
        
        if (connection.getData().getUsers() == null || connection.getData().getUsers().isEmpty()) {
            ServerManager.getInstance().getLogger().error("{} Users: None", connection.getName());
            return;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        connection.getData().getUsers().forEach(user -> {
            stringBuilder.append("\n");
            stringBuilder.append("Name: ").append(user.getName()).append("\n");
            stringBuilder.append("UniqueId: ").append(user.getUniqueId()).append("\n");
            stringBuilder.append("Address: ").append(user.getAddress());
        });
        
        ServerManager.getInstance().getLogger().info("{} Users: {}", connection.getName(), stringBuilder.toString());
    }
}