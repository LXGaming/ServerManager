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

package nz.co.lolnet.servermanager.sponge.configuration;

import nz.co.lolnet.servermanager.api.configuration.Config;
import nz.co.lolnet.servermanager.sponge.configuration.category.RedisCategory;

public class SpongeConfig implements Config {
    
    private boolean debug = false;
    private String name = "";
    private String host = "";
    private RedisCategory redisCategory = new RedisCategory();
    
    @Override
    public boolean isDebug() {
        return debug;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public String getHost() {
        return host;
    }
    
    public RedisCategory getRedisCategory() {
        return redisCategory;
    }
}