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

package io.github.lxgaming.servermanager.server.network.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.lxgaming.servermanager.api.entity.Instance;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.entity.InstanceImpl;
import io.github.lxgaming.servermanager.common.network.packet.ListPacket;
import io.github.lxgaming.servermanager.server.Server;
import io.github.lxgaming.servermanager.server.configuration.ConfigImpl;
import io.github.lxgaming.servermanager.server.configuration.category.InstanceCategory;
import io.github.lxgaming.servermanager.server.integration.client.ClientIntegration;
import io.github.lxgaming.servermanager.server.manager.IntegrationManager;
import io.github.lxgaming.servermanager.server.manager.NetworkManager;

import java.util.Set;

public class PacketUtils {
    
    public static ListPacket.Response createListResponse() {
        ClientIntegration integration = IntegrationManager.getIntegration(ClientIntegration.class);
        Preconditions.checkState(integration != null, "ClientIntegration is unavailable");
        
        Set<InstanceCategory> instanceCategories = Server.getInstance().getConfig().map(ConfigImpl::getInstanceCategories).orElse(null);
        Set<Instance> instances = Sets.newHashSet();
        if (instanceCategories == null || instanceCategories.isEmpty()) {
            return new ListPacket.Response(integration.getInstance(), instances);
        }
        
        for (InstanceCategory instanceCategory : instanceCategories) {
            Instance instance = NetworkManager.getInstance(instanceCategory.getId());
            if (instance != null) {
                instances.add(instance);
            } else {
                instances.add(new InstanceImpl(instanceCategory.getId(), instanceCategory.getName(), Platform.CLIENT));
            }
        }
        
        return new ListPacket.Response(integration.getInstance(), instances);
    }
}