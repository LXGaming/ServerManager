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

package io.github.lxgaming.servermanager.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.lxgaming.binary.tag.ByteArrayTag;
import io.github.lxgaming.binary.tag.ByteTag;
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.binary.tag.DoubleTag;
import io.github.lxgaming.binary.tag.FloatTag;
import io.github.lxgaming.binary.tag.IntArrayTag;
import io.github.lxgaming.binary.tag.IntTag;
import io.github.lxgaming.binary.tag.LongArrayTag;
import io.github.lxgaming.binary.tag.LongTag;
import io.github.lxgaming.binary.tag.ShortTag;
import io.github.lxgaming.binary.tag.StringTag;
import io.github.lxgaming.binary.tag.Tag;

import java.util.Map;

public class BinaryUtils {
    
    public static CompoundTag getCompoundTag(CompoundTag compoundTag, String path) {
        return getCompoundTag(compoundTag, StringUtils.split(path, '.'));
    }
    
    public static CompoundTag getCompoundTag(CompoundTag compoundTag, String... keys) {
        CompoundTag parentCompoundTag = compoundTag;
        for (String key : keys) {
            if (StringUtils.isBlank(key)) {
                continue;
            }
            
            parentCompoundTag = getOrCreateCompoundTag(parentCompoundTag, key);
        }
        
        return parentCompoundTag;
    }
    
    public static CompoundTag getOrCreateCompoundTag(CompoundTag compoundTag, String key) {
        Tag tag = compoundTag.get(key);
        if (tag == null) {
            CompoundTag newCompoundTag = new CompoundTag();
            compoundTag.put(key, newCompoundTag);
            return newCompoundTag;
        }
        
        return (CompoundTag) tag;
    }
    
    public static void mergeCompoundTags(CompoundTag fromCompound, String fromPath, CompoundTag toCompound, String toPath) {
        CompoundTag from = getCompoundTag(fromCompound, fromPath);
        CompoundTag to = getCompoundTag(toCompound, toPath);
        mergeCompoundTags(from, to);
    }
    
    public static void mergeCompoundTags(CompoundTag from, CompoundTag to) {
        for (Map.Entry<String, Tag> entry : from.entrySet()) {
            to.put(entry.getKey(), entry.getValue().copy());
        }
    }
    
    public static JsonObject toJson(CompoundTag compoundTag) {
        JsonObject jsonObject = new JsonObject();
        for (String key : compoundTag.keySet()) {
            Tag tag = compoundTag.get(key);
            if (tag == null) {
                // no-op
            } else if (tag instanceof ByteArrayTag) {
                JsonArray jsonArray = new JsonArray();
                for (Byte value : ((ByteArrayTag) tag).getValue()) {
                    jsonArray.add(value);
                }
                
                jsonObject.add(key, jsonArray);
            } else if (tag instanceof ByteTag) {
                jsonObject.addProperty(key, ((ByteTag) tag).getValue());
            } else if (tag instanceof CompoundTag) {
                jsonObject.add(key, toJson((CompoundTag) tag));
            } else if (tag instanceof DoubleTag) {
                jsonObject.addProperty(key, ((DoubleTag) tag).getValue());
            } else if (tag instanceof FloatTag) {
                jsonObject.addProperty(key, ((FloatTag) tag).getValue());
            } else if (tag instanceof IntArrayTag) {
                JsonArray jsonArray = new JsonArray();
                for (Integer value : ((IntArrayTag) tag).getValue()) {
                    jsonArray.add(value);
                }
                
                jsonObject.add(key, jsonArray);
            } else if (tag instanceof IntTag) {
                jsonObject.addProperty(key, ((IntTag) tag).getValue());
            } else if (tag instanceof LongArrayTag) {
                JsonArray jsonArray = new JsonArray();
                for (Long value : ((LongArrayTag) tag).getValue()) {
                    jsonArray.add(value);
                }
                
                jsonObject.add(key, jsonArray);
            } else if (tag instanceof LongTag) {
                jsonObject.addProperty(key, ((LongTag) tag).getValue());
            } else if (tag instanceof ShortTag) {
                jsonObject.addProperty(key, ((ShortTag) tag).getValue());
            } else if (tag instanceof StringTag) {
                jsonObject.addProperty(key, ((StringTag) tag).getValue());
            }
        }
        
        return jsonObject;
    }
}