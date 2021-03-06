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

package io.github.lxgaming.servermanager.server;

import io.github.lxgaming.servermanager.api.util.Reference;
import io.github.lxgaming.servermanager.server.configuration.ServerConfig;
import io.github.lxgaming.servermanager.server.manager.CommandManager;
import io.github.lxgaming.servermanager.server.util.TerminalConsoleAppender;

public class Main {
    
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        ServerManagerImpl.init();
        TerminalConsoleAppender.buildTerminal(Reference.NAME, ServerManagerImpl.getInstance().getConfig().map(ServerConfig::isJlineOverride).orElse(false));
        while (ServerManagerImpl.getInstance().isRunning()) {
            TerminalConsoleAppender.readline().ifPresent(CommandManager::process);
        }
        
        Runtime.getRuntime().exit(0);
    }
}