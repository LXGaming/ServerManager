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

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.ServerManagerImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class InformationCommand extends Command {
    
    @Override
    public boolean prepare() {
        addAlias("information");
        addAlias("info");
        addAlias("version");
        return true;
    }
    
    @Override
    public void execute(List<String> arguments) throws Exception {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(ServerManager.NAME).append(" v").append(ServerManager.VERSION).append("\n");
        stringBuilder.append("Uptime: ").append(Toolbox.getDuration(Duration.between(ServerManagerImpl.getInstance().getStartTime(), Instant.now()).toMillis())).append("\n");
        stringBuilder.append("Authors: ").append(ServerManager.AUTHORS).append("\n");
        stringBuilder.append("Website: ").append(ServerManager.WEBSITE).append("\n");
        stringBuilder.append("Source: ").append(ServerManager.SOURCE).append("\n");
        stringBuilder.append("Java Version: ").append(System.getProperty("java.version"));
        
        ServerManagerImpl.getInstance().getLogger().info(stringBuilder.toString());
    }
}