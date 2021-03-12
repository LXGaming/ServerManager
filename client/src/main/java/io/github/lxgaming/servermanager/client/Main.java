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

package io.github.lxgaming.servermanager.client;

import io.github.lxgaming.servermanager.common.ServerManagerImpl;
import io.github.lxgaming.servermanager.common.util.Toolbox;

public class Main {
    
    public static void main(String[] args) {
        Thread.currentThread().setName("Main Thread");
        System.setProperty("servermanager.logging.console.level", "DEBUG");
        ServerManagerImpl.init();
        Client client = new Client(Toolbox.getPath());
        if (!client.prepare()) {
            return;
        }
        
        client.execute();
        
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            // no-op
        }
    }
}