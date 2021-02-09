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

package io.github.lxgaming.servermanager.server.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.common.util.StringUtils;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.command.Command;
import io.github.lxgaming.servermanager.server.command.InformationCommand;
import io.github.lxgaming.servermanager.server.command.ShutdownCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public final class CommandManager {
    
    public static final Set<Command> COMMANDS = Sets.newLinkedHashSet();
    private static final Set<Class<? extends Command>> COMMAND_CLASSES = Sets.newHashSet();
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.NAME);
    
    public static void prepare() {
        registerCommand(InformationCommand.class);
        registerCommand(ShutdownCommand.class);
    }
    
    public static boolean execute(String message) {
        String content = parseMessage(message);
        if (StringUtils.isBlank(content)) {
            return false;
        }
        
        List<String> arguments = getArguments(content);
        if (arguments.isEmpty()) {
            return false;
        }
        
        Command command = getCommand(arguments);
        if (command == null) {
            return false;
        }
        
        try {
            command.execute(arguments);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while executing {}", Toolbox.getClassSimpleName(command.getClass()), ex);
            return false;
        }
    }
    
    public static boolean registerAlias(Command command, String alias) {
        if (StringUtils.containsIgnoreCase(command.getAliases(), alias)) {
            LOGGER.warn("{} is already registered for {}", alias, Toolbox.getClassSimpleName(command.getClass()));
            return false;
        }
        
        command.getAliases().add(alias);
        LOGGER.debug("{} registered for {}", alias, Toolbox.getClassSimpleName(command.getClass()));
        return true;
    }
    
    public static boolean registerCommand(Class<? extends Command> commandClass) {
        Command command = registerCommand(COMMANDS, commandClass);
        if (command != null) {
            LOGGER.debug("{} registered", Toolbox.getClassSimpleName(commandClass));
            return true;
        }
        
        return false;
    }
    
    public static boolean registerCommand(Command parentCommand, Class<? extends Command> commandClass) {
        if (parentCommand.getClass() == commandClass) {
            LOGGER.warn("{} attempted to register itself", Toolbox.getClassSimpleName(parentCommand.getClass()));
            return false;
        }
        
        Command command = registerCommand(parentCommand.getChildren(), commandClass);
        if (command != null) {
            command.parentCommand(parentCommand);
            LOGGER.debug("{} registered for {}", Toolbox.getClassSimpleName(commandClass), Toolbox.getClassSimpleName(parentCommand.getClass()));
            return true;
        }
        
        return false;
    }
    
    private static Command registerCommand(Set<Command> commands, Class<? extends Command> commandClass) {
        if (COMMAND_CLASSES.contains(commandClass)) {
            LOGGER.warn("{} is already registered", Toolbox.getClassSimpleName(commandClass));
            return null;
        }
        
        COMMAND_CLASSES.add(commandClass);
        Command command = Toolbox.newInstance(commandClass);
        if (command == null) {
            LOGGER.error("{} failed to initialize", Toolbox.getClassSimpleName(commandClass));
            return null;
        }
        
        try {
            if (!command.prepare()) {
                LOGGER.warn("{} failed to prepare", Toolbox.getClassSimpleName(commandClass));
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while preparing {}", Toolbox.getClassSimpleName(commandClass), ex);
            return null;
        }
        
        if (commands.add(command)) {
            return command;
        }
        
        return null;
    }
    
    public static Command getCommand(Class<? extends Command> commandClass) {
        return getCommand(null, commandClass);
    }
    
    public static Command getCommand(Command parentCommand, Class<? extends Command> commandClass) {
        Set<Command> commands = Sets.newLinkedHashSet();
        if (parentCommand != null) {
            commands.addAll(parentCommand.getChildren());
        } else {
            commands.addAll(COMMANDS);
        }
        
        for (Command command : commands) {
            if (command.getClass() == commandClass) {
                return command;
            }
            
            Command childCommand = getCommand(command, commandClass);
            if (childCommand != null) {
                return childCommand;
            }
        }
        
        return null;
    }
    
    public static Command getCommand(List<String> arguments) {
        return getCommand(null, arguments);
    }
    
    private static Command getCommand(Command parentCommand, List<String> arguments) {
        if (arguments.isEmpty()) {
            return parentCommand;
        }
        
        Set<Command> commands = Sets.newLinkedHashSet();
        if (parentCommand != null) {
            commands.addAll(parentCommand.getChildren());
        } else {
            commands.addAll(COMMANDS);
        }
        
        for (Command command : commands) {
            if (StringUtils.containsIgnoreCase(command.getAliases(), arguments.get(0))) {
                arguments.remove(0);
                return getCommand(command, arguments);
            }
        }
        
        return parentCommand;
    }
    
    private static List<String> getArguments(String string) {
        return Lists.newArrayList(string.split(" "));
    }
    
    private static String parseMessage(String message) {
        if (message.startsWith("/")) {
            return message.substring(1).trim();
        }
        
        return message.trim();
    }
}