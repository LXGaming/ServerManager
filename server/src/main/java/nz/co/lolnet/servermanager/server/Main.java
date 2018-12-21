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

package nz.co.lolnet.servermanager.server;

import nz.co.lolnet.servermanager.api.util.Logger;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.server.configuration.ServerConfig;
import nz.co.lolnet.servermanager.server.manager.CommandManager;
import nz.co.lolnet.servermanager.server.util.TerminalConsoleAppender;
import org.apache.logging.log4j.LogManager;

public class Main {
    
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger()
                .add(Logger.Level.INFO, LogManager.getLogger(Reference.ID)::info)
                .add(Logger.Level.WARN, LogManager.getLogger(Reference.ID)::warn)
                .add(Logger.Level.ERROR, LogManager.getLogger(Reference.ID)::error)
                .add(Logger.Level.DEBUG, LogManager.getLogger(Reference.ID)::debug);
        
        serverManager.loadServerManager();
        serverManager.reloadServerManager();
        
        TerminalConsoleAppender.buildTerminal(Reference.NAME, serverManager.getConfig().map(ServerConfig::isJlineOverride).orElse(false));
        while (serverManager.getRunning().get()) {
            TerminalConsoleAppender.readline().ifPresent(CommandManager::process);
        }
    }
}