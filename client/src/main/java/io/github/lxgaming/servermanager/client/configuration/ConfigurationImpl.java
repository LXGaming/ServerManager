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

package io.github.lxgaming.servermanager.client.configuration;

import io.github.lxgaming.servermanager.common.configuration.Configuration;

import java.nio.file.Path;

public class ConfigurationImpl extends Configuration {
    
    public ConfigurationImpl(Path path) {
        super(path, ConfigImpl.class);
    }
    
    @Override
    public ConfigImpl getConfig() {
        return (ConfigImpl) super.getConfig();
    }
}