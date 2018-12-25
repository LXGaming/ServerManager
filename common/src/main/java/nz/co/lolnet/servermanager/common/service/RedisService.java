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

package nz.co.lolnet.servermanager.common.service;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public abstract class RedisService extends AbstractService {
    
    private final Set<String> channels = Toolbox.newHashSet();
    
    @Override
    public boolean prepareService() {
        getChannels().add(Reference.ID + "-" + ServerManager.getInstance().getPlatformType());
        ServerManager.getInstance().getConfig()
                .map(Config::getName)
                .map(name -> Toolbox.createChannel(ServerManager.getInstance().getPlatformType(), name))
                .ifPresent(getChannels()::add);
        return true;
    }
    
    public abstract void shutdown();
    
    public abstract void publish(String channel, String message);
    
    public abstract String clientList();
    
    public List<Properties> getClientList() {
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
            
            clientNames.add(clientName);
        }
        
        return clientNames;
    }
    
    public Set<String> getChannels() {
        return channels;
    }
}