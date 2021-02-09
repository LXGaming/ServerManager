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

package io.github.lxgaming.servermanager.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.lxgaming.servermanager.common.util.StringUtils;
import io.github.lxgaming.servermanager.server.manager.CommandManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class Command {
    
    private final Set<String> aliases = Sets.newLinkedHashSet();
    private final Set<Command> children = Sets.newLinkedHashSet();
    private Command parentCommand;
    private String description;
    private String usage;
    
    public abstract boolean prepare();
    
    public abstract void execute(List<String> arguments) throws Exception;
    
    public final Optional<String> getPrimaryAlias() {
        for (String alias : aliases) {
            if (StringUtils.isNotBlank(alias)) {
                return Optional.of(alias);
            }
        }
        
        return Optional.empty();
    }
    
    public final List<String> getPath() {
        List<String> paths = Lists.newArrayList();
        if (parentCommand != null) {
            paths.addAll(parentCommand.getPath());
        }
        
        getPrimaryAlias().ifPresent(paths::add);
        return paths;
    }
    
    protected final void addAlias(String alias) {
        CommandManager.registerAlias(this, alias);
    }
    
    public final Set<String> getAliases() {
        return aliases;
    }
    
    protected final void addChild(Class<? extends Command> commandClass) {
        CommandManager.registerCommand(this, commandClass);
    }
    
    public final Set<Command> getChildren() {
        return children;
    }
    
    public final Command getParentCommand() {
        return parentCommand;
    }
    
    public final void parentCommand(Command parentCommand) {
        if (this.parentCommand != null) {
            throw new IllegalStateException("ParentCommand is already set");
        }
        
        this.parentCommand = parentCommand;
    }
    
    public final String getDescription() {
        return description;
    }
    
    protected final void description(String description) {
        if (this.description != null) {
            throw new IllegalStateException("Description is already set");
        }
        
        this.description = description;
    }
    
    public final String getUsage() {
        return usage;
    }
    
    protected final void usage(String usage) {
        if (this.usage != null) {
            throw new IllegalStateException("Usage is already set");
        }
        
        this.usage = usage;
    }
}