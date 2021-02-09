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

package io.github.lxgaming.servermanager.server.util.terminalconsole;

import io.github.lxgaming.servermanager.server.ServerManagerImpl;
import io.github.lxgaming.servermanager.server.manager.CommandManager;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import net.minecrell.terminalconsole.TerminalConsoleAppender;

import java.io.IOException;

public class TerminalConsole extends SimpleTerminalConsole {
    
    @Override
    protected boolean isRunning() {
        return ServerManagerImpl.getInstance().getState().get();
    }
    
    @Override
    protected void runCommand(String command) {
        CommandManager.execute(command);
    }
    
    @Override
    protected void shutdown() {
        ServerManagerImpl.getInstance().getState().set(false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        super.start();
        
        try {
            // The TerminalConsoleAppender must be manually closed otherwise logging messages will not appear.
            TerminalConsoleAppender.close();
        } catch (IOException ex) {
            ServerManagerImpl.getInstance().getLogger().error("Failed to close TerminalConsoleAppender, continuing with shutdown process...");
        }
    }
}