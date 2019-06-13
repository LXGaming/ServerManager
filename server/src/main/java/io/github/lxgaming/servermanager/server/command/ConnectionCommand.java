/*
 * Copyright 2019 Alex Thomson
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

import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.command.connection.ListConnectionCommand;
import io.github.lxgaming.servermanager.server.command.connection.StatusConnectionCommand;
import io.github.lxgaming.servermanager.server.command.connection.UserConnectionCommand;
import io.github.lxgaming.servermanager.server.manager.CommandManager;

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