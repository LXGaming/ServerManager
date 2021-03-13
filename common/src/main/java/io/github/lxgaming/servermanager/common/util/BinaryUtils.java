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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.lxgaming.binary.tag.BooleanTag;
import io.github.lxgaming.binary.tag.ByteArrayTag;
import io.github.lxgaming.binary.tag.ByteTag;
import io.github.lxgaming.binary.tag.CompoundTag;
import io.github.lxgaming.binary.tag.DoubleArrayTag;
import io.github.lxgaming.binary.tag.DoubleTag;
import io.github.lxgaming.binary.tag.FloatArrayTag;
import io.github.lxgaming.binary.tag.FloatTag;
import io.github.lxgaming.binary.tag.IntArrayTag;
import io.github.lxgaming.binary.tag.IntTag;
import io.github.lxgaming.binary.tag.ListTag;
import io.github.lxgaming.binary.tag.LongArrayTag;
import io.github.lxgaming.binary.tag.LongTag;
import io.github.lxgaming.binary.tag.ShortArrayTag;
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
            JsonElement element = toJson(tag);
            jsonObject.add(key, element);
        }
        
        return jsonObject;
    }
    
    public static JsonElement toJson(Tag tag) {
        if (tag == null) {
            return null;
        } else if (tag instanceof BooleanTag) {
            return new JsonPrimitive(((BooleanTag) tag).getValue());
        } else if (tag instanceof ByteArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (Byte value : ((ByteArrayTag) tag).getValue()) {
                jsonArray.add(value);
            }
            
            return jsonArray;
        } else if (tag instanceof ByteTag) {
            return new JsonPrimitive(((ByteTag) tag).getValue());
        } else if (tag instanceof CompoundTag) {
            return toJson((CompoundTag) tag);
        } else if (tag instanceof DoubleArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (Double value : ((DoubleArrayTag) tag).getValue()) {
                jsonArray.add(value);
            }
            
            return jsonArray;
        } else if (tag instanceof DoubleTag) {
            return new JsonPrimitive(((DoubleTag) tag).getValue());
        } else if (tag instanceof FloatArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (Float value : ((FloatArrayTag) tag).getValue()) {
                jsonArray.add(value);
            }
            
            return jsonArray;
        } else if (tag instanceof FloatTag) {
            return new JsonPrimitive(((FloatTag) tag).getValue());
        } else if (tag instanceof IntArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (Integer value : ((IntArrayTag) tag).getValue()) {
                jsonArray.add(value);
            }
            
            return jsonArray;
        } else if (tag instanceof IntTag) {
            return new JsonPrimitive(((IntTag) tag).getValue());
        } else if (tag instanceof ListTag) {
            JsonArray jsonArray = new JsonArray();
            for (Tag value : (ListTag) tag) {
                jsonArray.add(toJson(value));
            }
            
            return jsonArray;
        } else if (tag instanceof LongArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (Long value : ((LongArrayTag) tag).getValue()) {
                jsonArray.add(value);
            }
            
            return jsonArray;
        } else if (tag instanceof LongTag) {
            return new JsonPrimitive(((LongTag) tag).getValue());
        } else if (tag instanceof ShortArrayTag) {
            JsonArray jsonArray = new JsonArray();
            for (Short value : ((ShortArrayTag) tag).getValue()) {
                jsonArray.add(value);
            }
            
            return jsonArray;
        } else if (tag instanceof ShortTag) {
            return new JsonPrimitive(((ShortTag) tag).getValue());
        } else if (tag instanceof StringTag) {
            return new JsonPrimitive(((StringTag) tag).getValue());
        } else {
            throw new UnsupportedOperationException(String.format("%s is not supported", tag.getClass().getSimpleName()));
        }
    }
}