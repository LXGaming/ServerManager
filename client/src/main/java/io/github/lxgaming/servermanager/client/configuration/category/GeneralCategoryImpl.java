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

package io.github.lxgaming.servermanager.client.configuration.category;

import com.google.gson.annotations.SerializedName;
import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.common.configuration.category.GeneralCategory;

import java.util.UUID;

public class GeneralCategoryImpl extends GeneralCategory {
    
    @SerializedName("id")
    private UUID id = UUID.randomUUID();
    
    @SerializedName("name")
    private String name = "Unknown";
    
    @SerializedName("platform")
    private Platform platform = Platform.UNKNOWN;
    
    @SerializedName("path")
    private String path = "";
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
}