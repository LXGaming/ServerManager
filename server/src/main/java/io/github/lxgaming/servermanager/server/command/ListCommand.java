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

import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.util.BinaryUtils;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;

import java.util.List;

public class ListCommand extends Command {
    
    @Override
    public boolean prepare() {
        addAlias("list");
        return true;
    }
    
    @Override
    public void execute(List<String> arguments) throws Exception {
        if (NetworkManager.CONNECTIONS.isEmpty()) {
            Server.getInstance().getLogger().info("No connections");
            return;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Connections:");
        for (Connection connection : NetworkManager.CONNECTIONS) {
            Instance instance = connection.getInstance();
            stringBuilder.append("\n");
            stringBuilder.append(instance.getName()).append(" (").append(instance.getId()).append("):\n");
            stringBuilder.append(Toolbox.GSON.toJson(BinaryUtils.toJson(instance.getData())).replace("\\n", "\n"));
        }
        
        Server.getInstance().getLogger().info(stringBuilder.toString());
    }
}