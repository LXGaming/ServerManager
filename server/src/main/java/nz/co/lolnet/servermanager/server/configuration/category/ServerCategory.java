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

package nz.co.lolnet.servermanager.server.configuration.category;

import nz.co.lolnet.servermanager.api.data.Platform;

public class ServerCategory {
    
    private String name = "Unknown";
    private Platform.Type platform = Platform.Type.UNKNOWN;
    private String path = "";
    private boolean autoRestart = false;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Platform.Type getPlatform() {
        return platform;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean isAutoRestart() {
        return autoRestart;
    }
}