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

package io.github.lxgaming.servermanager.server.listener;

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.event.network.ConnectionEventImpl;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import net.kyori.event.method.annotation.Subscribe;

public class NetworkListener {
    
    @Subscribe
    public void onConnect(ConnectionEventImpl.Connect event) {
        if (event.getPlatform() != Platform.CLIENT) {
            return;
        }
        
        NetworkManager.CONNECTIONS.add(event.getConnection());
    }
    
    @Subscribe
    public void onDisconnect(ConnectionEventImpl.Disconnect event) {
        if (event.getPlatform() != Platform.CLIENT) {
            return;
        }
        
        NetworkManager.CONNECTIONS.remove(event.getConnection());
    }
}