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

package nz.co.lolnet.servermanager.server.commands;

import nz.co.lolnet.servermanager.common.util.Reference;
import nz.co.lolnet.servermanager.server.ServerManager;

import java.util.List;

public class InfoCommand extends AbstractCommand {
    
    public InfoCommand() {
        addAlias("info");
        addAlias("version");
        setDescription("Application information");
    }
    
    @Override
    public void execute(List<String> arguments) {
        ServerManager.getInstance().getLogger().info("{} v{}", Reference.NAME, Reference.VERSION);
        ServerManager.getInstance().getLogger().info("Authors: {}", Reference.AUTHORS);
        ServerManager.getInstance().getLogger().info("Source: {}", Reference.SOURCE);
        ServerManager.getInstance().getLogger().info("Website: {}", Reference.WEBSITE);
    }
}