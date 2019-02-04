/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.servermanager.server.manager;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import nz.co.lolnet.servermanager.server.command.AbstractCommand;
import nz.co.lolnet.servermanager.server.command.ConnectionCommand;
import nz.co.lolnet.servermanager.server.command.ExecuteCommand;
import nz.co.lolnet.servermanager.server.command.HelpCommand;
import nz.co.lolnet.servermanager.server.command.InfoCommand;
import nz.co.lolnet.servermanager.server.command.MessageCommand;
import nz.co.lolnet.servermanager.server.command.PingCommand;
import nz.co.lolnet.servermanager.server.command.ReloadCommand;
import nz.co.lolnet.servermanager.server.command.StopCommand;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CommandManager {
    
    private static final Set<AbstractCommand> COMMANDS = Toolbox.newLinkedHashSet();
    private static final Set<Class<? extends AbstractCommand>> COMMAND_CLASSES = Toolbox.newLinkedHashSet();
    
    public static void buildCommands() {
        registerCommand(ConnectionCommand.class);
        registerCommand(ExecuteCommand.class);
        registerCommand(HelpCommand.class);
        registerCommand(InfoCommand.class);
        registerCommand(MessageCommand.class);
        registerCommand(PingCommand.class);
        registerCommand(ReloadCommand.class);
        registerCommand(StopCommand.class);
    }
    
    public static boolean process(String message) {
        List<String> arguments = getArguments(message).map(Toolbox::newArrayList).orElse(null);
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        
        AbstractCommand command = getCommand(arguments).orElse(null);
        if (command == null) {
            ServerManager.getInstance().getLogger().error("Unknown command. Try help for a list of commands");
            return false;
        }
        
        ServerManager.getInstance().getLogger().debug("Processing {}", command.getPrimaryAlias().orElse("Unknown"));
        
        try {
            command.execute(arguments);
            return true;
        } catch (Exception ex) {
            ServerManager.getInstance().getLogger().error("Encountered an error processing {}::process", "CommandManager", ex);
            return false;
        }
    }
    
    public static boolean registerCommand(Class<? extends AbstractCommand> commandClass) {
        if (getCommandClasses().contains(commandClass)) {
            ServerManager.getInstance().getLogger().warn("{} is already registered", commandClass.getSimpleName());
            return false;
        }
        
        getCommandClasses().add(commandClass);
        AbstractCommand command = Toolbox.newInstance(commandClass).orElse(null);
        if (command == null) {
            ServerManager.getInstance().getLogger().error("{} failed to initialize", commandClass.getSimpleName());
            return false;
        }
        
        getCommands().add(command);
        ServerManager.getInstance().getLogger().debug("{} registered", commandClass.getSimpleName());
        return true;
    }
    
    public static boolean registerAlias(AbstractCommand command, String alias) {
        if (Toolbox.containsIgnoreCase(command.getAliases(), alias)) {
            ServerManager.getInstance().getLogger().warn("{} is already registered for {}", alias, command.getClass().getSimpleName());
            return false;
        }
        
        command.getAliases().add(alias);
        ServerManager.getInstance().getLogger().debug("{} registered for {}", alias, command.getClass().getSimpleName());
        return true;
    }
    
    public static boolean registerCommand(AbstractCommand parentCommand, Class<? extends AbstractCommand> commandClass) {
        if (parentCommand.getClass() == commandClass) {
            ServerManager.getInstance().getLogger().warn("{} attempted to register itself", parentCommand.getClass().getSimpleName());
            return false;
        }
        
        if (getCommandClasses().contains(commandClass)) {
            ServerManager.getInstance().getLogger().warn("{} is already registered", commandClass.getSimpleName());
            return false;
        }
        
        getCommandClasses().add(commandClass);
        AbstractCommand command = Toolbox.newInstance(commandClass).orElse(null);
        if (command == null) {
            ServerManager.getInstance().getLogger().error("{} failed to initialize", commandClass.getSimpleName());
            return false;
        }
        
        parentCommand.getChildren().add(command);
        ServerManager.getInstance().getLogger().debug("{} registered for {}", commandClass.getSimpleName(), parentCommand.getClass().getSimpleName());
        return true;
    }
    
    public static Optional<AbstractCommand> getCommand(List<String> arguments) {
        return getCommand(null, arguments);
    }
    
    private static Optional<AbstractCommand> getCommand(AbstractCommand parentCommand, List<String> arguments) {
        Set<AbstractCommand> commands = Toolbox.newLinkedHashSet();
        if (parentCommand != null) {
            commands.addAll(parentCommand.getChildren());
        } else {
            commands.addAll(getCommands());
        }
        
        if (arguments.isEmpty() || commands.isEmpty()) {
            return Optional.ofNullable(parentCommand);
        }
        
        for (AbstractCommand command : commands) {
            if (Toolbox.containsIgnoreCase(command.getAliases(), arguments.get(0))) {
                arguments.remove(0);
                return getCommand(command, arguments);
            }
        }
        
        return Optional.ofNullable(parentCommand);
    }
    
    private static Optional<String[]> getArguments(String message) {
        if (message.startsWith("/")) {
            return Optional.of(Toolbox.filter(message.substring(1)).split(" "));
        }
        
        return Optional.of(Toolbox.filter(message).split(" "));
    }
    
    public static Set<AbstractCommand> getCommands() {
        return COMMANDS;
    }
    
    private static Set<Class<? extends AbstractCommand>> getCommandClasses() {
        return COMMAND_CLASSES;
    }
}