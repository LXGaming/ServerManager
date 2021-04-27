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

package io.github.lxgaming.servermanager.server;

import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.util.terminalconsole.TerminalConsole;

public class Main {
    
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        if (System.getProperty("log4j.skipJansi") == null) {
            System.setProperty("log4j.skipJansi", "false");
        }
        
        ServerManagerImpl.init();
        Server server = new Server(Toolbox.getPath());
        if (!server.prepare()) {
            return;
        }
        
        server.execute();
        
        new TerminalConsole().start();
    }
}