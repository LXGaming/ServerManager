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

package io.github.lxgaming.servermanager.server.manager;

import com.google.common.collect.Sets;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import io.github.lxgaming.servermanager.server.integration.Integration;
import io.github.lxgaming.servermanager.server.integration.client.ClientIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public final class IntegrationManager {
    
    private static final Set<Integration> INTEGRATIONS = Sets.newLinkedHashSet();
    private static final Set<Class<? extends Integration>> INTEGRATION_CLASSES = Sets.newHashSet();
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationManager.class);
    
    public static void prepare() {
        registerIntegration(ClientIntegration.class);
    }
    
    public static void execute() {
        for (Integration integration : INTEGRATIONS) {
            try {
                integration.execute();
            } catch (Exception ex) {
                LOGGER.error("Encountered an error while executing {}", Toolbox.getClassSimpleName(integration.getClass()), ex);
            }
        }
    }
    
    public static void shutdown() {
        for (Integration integration : INTEGRATIONS) {
            try {
                integration.shutdown();
            } catch (Exception ex) {
                LOGGER.error("Encountered an error while shutting down {}", Toolbox.getClassSimpleName(integration.getClass()), ex);
            }
        }
    }
    
    public static boolean registerIntegration(Class<? extends Integration> integrationClass) {
        if (INTEGRATION_CLASSES.contains(integrationClass)) {
            LOGGER.warn("{} is already registered", Toolbox.getClassSimpleName(integrationClass));
            return false;
        }
        
        INTEGRATION_CLASSES.add(integrationClass);
        Integration integration = Toolbox.newInstance(integrationClass);
        if (integration == null) {
            LOGGER.error("{} failed to initialize", Toolbox.getClassSimpleName(integrationClass));
            return false;
        }
        
        try {
            if (!integration.prepare()) {
                LOGGER.warn("{} failed to prepare", Toolbox.getClassSimpleName(integrationClass));
                return false;
            }
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while preparing {}", Toolbox.getClassSimpleName(integrationClass), ex);
            return false;
        }
        
        INTEGRATIONS.add(integration);
        LOGGER.debug("{} registered", Toolbox.getClassSimpleName(integrationClass));
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Integration> T getIntegration(Class<T> integrationClass) {
        for (Integration integration : INTEGRATIONS) {
            if (integration.getClass() == integrationClass) {
                return (T) integration;
            }
        }
        
        return null;
    }
}