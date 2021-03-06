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

package io.github.lxgaming.servermanager.common.service;

import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.configuration.Config;
import io.github.lxgaming.servermanager.api.util.Reference;
import io.github.lxgaming.servermanager.common.util.Toolbox;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class RedisService extends AbstractService {
    
    private final Set<String> channels = Toolbox.newHashSet();
    
    @Override
    public boolean prepare() {
        getChannels().add(Toolbox.createChannel(Toolbox.createId(ServerManager.getInstance().getPlatformType())));
        ServerManager.getInstance().getConfig()
                .map(Config::getName)
                .map(name -> Toolbox.createId(ServerManager.getInstance().getPlatformType(), name))
                .map(Toolbox::createChannel)
                .ifPresent(getChannels()::add);
        
        return true;
    }
    
    public abstract void shutdown();
    
    public abstract void publish(String channel, String message);
    
    protected abstract String clientList();
    
    protected List<Properties> getClientList() {
        List<Properties> clients = Toolbox.newArrayList();
        String clientList = clientList();
        if (Toolbox.isBlank(clientList)) {
            return clients;
        }
        
        for (String line : clientList.split("\n")) {
            if (Toolbox.isBlank(line)) {
                continue;
            }
            
            try (StringReader stringReader = new StringReader(line.replace(" ", "\n"))) {
                Properties properties = new Properties();
                properties.load(stringReader);
                clients.add(properties);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return clients;
    }
    
    public List<String> getClientNames() {
        List<String> clientNames = Toolbox.newArrayList();
        for (Properties properties : getClientList()) {
            String clientName = properties.getProperty("name");
            if (Toolbox.isBlank(clientName) || !clientName.startsWith(Reference.ID)) {
                continue;
            }
            
            clientNames.add(Toolbox.getId(clientName));
        }
        
        return clientNames;
    }
    
    public Set<String> getChannels() {
        return channels;
    }
}