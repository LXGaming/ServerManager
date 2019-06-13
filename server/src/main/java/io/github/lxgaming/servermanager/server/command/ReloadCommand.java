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

import io.github.lxgaming.servermanager.server.ServerManagerImpl;

import java.util.List;

public class ReloadCommand extends AbstractCommand {
    
    public ReloadCommand() {
        addAlias("reload");
        setDescription("Reloads the application configuration");
    }
    
    @Override
    public void execute(List<String> arguments) {
        ServerManagerImpl.getInstance().getConfiguration().loadConfiguration();
        ServerManagerImpl.getInstance().reloadServerManager();
        ServerManagerImpl.getInstance().getConfiguration().saveConfiguration();
        ServerManagerImpl.getInstance().getLogger().info("Reloaded");
    }
}