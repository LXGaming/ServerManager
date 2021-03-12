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

package io.github.lxgaming.servermanager.common.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class Configuration {
    
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    protected static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    
    protected final Path path;
    protected Config config;
    
    protected Configuration(@NonNull Path path) {
        this.path = path;
    }
    
    public abstract boolean loadConfiguration();
    
    public abstract boolean saveConfiguration();
    
    protected static <T> @Nullable T loadFile(@NonNull Path path, @NonNull Class<T> type) {
        if (Files.exists(path)) {
            return deserializeFile(path, type);
        }
        
        T object = Toolbox.newInstance(type);
        if (object != null && saveFile(path, object)) {
            return object;
        }
        
        return null;
    }
    
    protected static boolean saveFile(@NonNull Path path, @Nullable Object object) {
        if (Files.exists(path) || createFile(path)) {
            return serializeFile(path, object);
        }
        
        return false;
    }
    
    protected static <T> @Nullable T deserializeFile(@NonNull Path path, @NonNull Class<T> type) {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, type);
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while deserializing {}", path, ex);
            return null;
        }
    }
    
    protected static boolean serializeFile(@NonNull Path path, @Nullable Object object) {
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            GSON.toJson(object, writer);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while serializing {}", path, ex);
            return false;
        }
    }
    
    protected static boolean createFile(@NonNull Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            
            Files.createFile(path);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Encountered an error while creating {}", path, ex);
            return false;
        }
    }
    
    public @Nullable Config getConfig() {
        return config;
    }
}