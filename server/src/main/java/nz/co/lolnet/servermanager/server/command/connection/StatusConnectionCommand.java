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

import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.server.command.AbstractCommand;
import nz.co.lolnet.servermanager.server.data.Connection;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

import java.util.List;

public class StatusConnectionCommand extends AbstractCommand {
    
    public StatusConnectionCommand() {
        addAlias("status");
        setDescription("View information regarding the specified connection");
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
        
        StringBuilder stringBuilder = new StringBuilder("\n");
        if (connection.getData().getLastTickTime() != null) {
            stringBuilder.append("LastTickTime: ").append(connection.getData().getLastTickTime()).append("\n");
        }
        
        stringBuilder.append("StartTime: ").append(connection.getData().getStartTime()).append("\n");
        stringBuilder.append("State: ");
        if (connection.getData().getState() != null) {
            stringBuilder.append(connection.getData().getState().getName());
        } else {
            stringBuilder.append(Platform.State.UNKNOWN.getName());
        }
        
        stringBuilder.append("\n");
        if (connection.getData().getTicksPerSecond() != null) {
            stringBuilder.append("TicksPerSecond: ").append(connection.getData().getLastTickTime()).append("\n");
        }
        
        stringBuilder.append("Version: ").append(connection.getData().getVersion());
        ServerManager.getInstance().getLogger().info("{} Status: {}", connection.getName(), stringBuilder.toString());
    }
}