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

package nz.co.lolnet.servermanager.server.command;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.packet.MessagePacket;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;
import nz.co.lolnet.servermanager.server.data.Connection;
import nz.co.lolnet.servermanager.server.manager.ConnectionManager;

import java.util.List;

public class MessageCommand extends AbstractCommand {
    
    public MessageCommand() {
        addAlias("message");
        setDescription("Sends a message to the target connection");
        setUsage("<Connection> <Position> <Message>");
    }
    
    @Override
    public void execute(List<String> arguments) {
        if (arguments.size() < 3) {
            ServerManager.getInstance().getLogger().error("Not enough arguments");
            return;
        }
        
        Connection connection = ConnectionManager.getConnection(arguments.remove(0)).orElse(null);
        if (connection == null) {
            ServerManager.getInstance().getLogger().error("Failed to find connection");
            return;
        }
        
        MessagePacket.Position position;
        
        try {
            position = MessagePacket.Position.valueOf(arguments.remove(0));
        } catch (IllegalArgumentException ex) {
            ServerManager.getInstance().getLogger().error("Invalid position");
            return;
        }
        
        String message = String.join(" ", arguments);
        if (Toolbox.isBlank(message)) {
            ServerManager.getInstance().getLogger().error("Message cannot be blank");
            return;
        }
        
        ServerManagerImpl.getInstance().sendRequest(connection.getId(), new MessagePacket(message, "servermanager.message.base", position));
        ServerManager.getInstance().getLogger().info("Message sent");
    }
}