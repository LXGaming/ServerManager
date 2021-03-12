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
import io.github.lxgaming.servermanager.common.configuration.category.NetworkCategory;

public class NetworkCategoryImpl extends NetworkCategory {
    
    public static final int DEFAULT_MAXIMUM_RECONNECT_DELAY = 300000; // 5 Minutes
    public static final int DEFAULT_MINIMUM_RECONNECT_DELAY = 1000; // 1 Second
    
    @SerializedName("reconnect")
    private boolean reconnect = true;
    
    @SerializedName("maximumReconnectDelay")
    private int maximumReconnectDelay = DEFAULT_MAXIMUM_RECONNECT_DELAY;
    
    public boolean isReconnect() {
        return reconnect;
    }
    
    public int getMaximumReconnectDelay() {
        return maximumReconnectDelay;
    }
    
    public void setMaximumReconnectDelay(int maximumReconnectDelay) {
        this.maximumReconnectDelay = maximumReconnectDelay;
    }
}