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

package io.github.lxgaming.servermanager.server.integration.client;

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.event.lifecycle.LifecycleEvent;
import io.github.lxgaming.servermanager.client.Client;
import io.github.lxgaming.servermanager.client.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.client.configuration.category.GeneralCategoryImpl;
import io.github.lxgaming.servermanager.common.event.network.ConnectionEventImpl;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;
import net.kyori.event.method.annotation.Subscribe;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientListener {
    
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
    
    @Subscribe
    public void onInitialize(LifecycleEvent.Initialize event) {
        if (event.getPlatform() != Platform.CLIENT) {
            return;
        }
        
        GeneralCategoryImpl category = Client.getInstance().getConfig().map(ConfigImpl::getGeneralCategory).orElseThrow(NullPointerException::new);
        if (category.getName() == null || category.getName().equalsIgnoreCase("Unknown")) {
            String name = getHostName();
            if (name != null && !name.equals(category.getName())) {
                category.setName(name);
            }
        }
        
        category.setPlatform(Platform.SERVER);
    }
    
    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return null;
        }
    }
}