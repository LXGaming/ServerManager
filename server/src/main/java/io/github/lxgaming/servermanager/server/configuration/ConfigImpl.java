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

package io.github.lxgaming.servermanager.server.configuration;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import io.github.lxgaming.servermanager.common.configuration.Config;
import io.github.lxgaming.servermanager.common.configuration.category.GeneralCategory;
import io.github.lxgaming.servermanager.server.configuration.category.InstanceCategory;
import io.github.lxgaming.servermanager.server.configuration.category.NetworkCategoryImpl;
import io.github.lxgaming.servermanager.server.configuration.category.TaskCategory;

import java.util.Set;

public class ConfigImpl implements Config {
    
    @SerializedName("general")
    private GeneralCategory generalCategory = new GeneralCategory();
    
    @SerializedName("instances")
    private Set<InstanceCategory> instanceCategories = Sets.newCopyOnWriteArraySet();
    
    @SerializedName("network")
    private NetworkCategoryImpl networkCategory = new NetworkCategoryImpl();
    
    @SerializedName("task")
    private TaskCategory taskCategory = new TaskCategory();
    
    @Override
    public GeneralCategory getGeneralCategory() {
        return generalCategory;
    }
    
    public Set<InstanceCategory> getInstanceCategories() {
        return instanceCategories;
    }
    
    @Override
    public NetworkCategoryImpl getNetworkCategory() {
        return networkCategory;
    }
    
    public TaskCategory getTaskCategory() {
        return taskCategory;
    }
}