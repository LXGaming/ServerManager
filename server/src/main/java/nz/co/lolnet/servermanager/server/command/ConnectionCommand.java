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

import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.command.connection.ListConnectionCommand;
import nz.co.lolnet.servermanager.server.command.connection.StatusConnectionCommand;
import nz.co.lolnet.servermanager.server.command.connection.UserConnectionCommand;
import nz.co.lolnet.servermanager.server.manager.CommandManager;

import java.util.List;

public class ConnectionCommand extends AbstractCommand {
    
    public ConnectionCommand() {
        addAlias("connection");
        addAlias("connections");
        addChild(ListConnectionCommand.class);
        addChild(StatusConnectionCommand.class);
        addChild(UserConnectionCommand.class);
        setDescription("No description provided");
    }
    
    @Override
    public void execute(List<String> arguments) {
        CommandManager.getCommand(HelpCommand.class).ifPresent(command -> command.execute(getPrimaryAlias().map(Toolbox::newArrayList).orElseGet(Toolbox::newArrayList)));
    }
}