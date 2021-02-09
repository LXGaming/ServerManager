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
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.EndBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;

public class NBTUtils {
    
    public static CompoundBinaryTag getCompoundTag(CompoundBinaryTag compoundTag, String path) {
        if (StringUtils.isBlank(path)) {
            return compoundTag;
        }
        
        CompoundBinaryTag parentTag = compoundTag;
        for (String key : path.split("\\.")) {
            parentTag = getOrCreateCompoundTag(parentTag, key);
        }
        
        return parentTag;
    }
    
    public static BinaryTag getTag(String path, CompoundBinaryTag compoundTag) {
        if (StringUtils.isBlank(path)) {
            return compoundTag;
        }
        
        BinaryTag parentTag = compoundTag;
        for (String key : path.split("\\.")) {
            if (parentTag instanceof CompoundBinaryTag) {
                BinaryTag childTag = ((CompoundBinaryTag) parentTag).get(key);
                if (childTag == null) {
                    CompoundBinaryTag newCompoundTag = CompoundBinaryTag.builder().build();
                    ((CompoundBinaryTag) parentTag).put(key, newCompoundTag);
                    parentTag = newCompoundTag;
                    continue;
                }
                
                parentTag = childTag;
                continue;
            }
            
            throw new UnsupportedOperationException(String.format("Unsupported BinaryTag (got %s, expected CompoundBinaryTag)", parentTag.getClass().getSimpleName()));
        }
        
        return parentTag;
    }
    
    public static CompoundBinaryTag getOrCreateCompoundTag(CompoundBinaryTag compoundTag, String key) {
        BinaryTag tag = compoundTag.get(key);
        if (tag == null) {
            CompoundBinaryTag newCompoundTag = CompoundBinaryTag.builder().build();
            compoundTag.put(key, newCompoundTag);
            return newCompoundTag;
        }
        
        return (CompoundBinaryTag) tag;
    }
    
    @SuppressWarnings("ConstantConditions")
    public static void mergeCompoundTags(CompoundBinaryTag targetCompoundTag, CompoundBinaryTag compoundTag) {
        for (String key : compoundTag.keySet()) {
            targetCompoundTag.put(key, compoundTag.get(key));
        }
    }
    
    public static JsonObject toJson(CompoundBinaryTag compoundTag) {
        JsonObject jsonObject = new JsonObject();
        for (String key : compoundTag.keySet()) {
            BinaryTag tag = compoundTag.get(key);
            if (tag == null) {
                // no-op
            } else if (tag instanceof ByteArrayBinaryTag) {
                JsonArray jsonArray = new JsonArray();
                for (Byte value : ((ByteArrayBinaryTag) tag)) {
                    jsonArray.add(value);
                }
                
                jsonObject.add(key, jsonArray);
            } else if (tag instanceof ByteBinaryTag) {
                jsonObject.addProperty(key, ((ByteBinaryTag) tag).byteValue());
            } else if (tag instanceof CompoundBinaryTag) {
                jsonObject.add(key, toJson((CompoundBinaryTag) tag));
            } else if (tag instanceof DoubleBinaryTag) {
                jsonObject.addProperty(key, ((DoubleBinaryTag) tag).doubleValue());
            } else if (tag instanceof EndBinaryTag) {
                // no-op
            } else if (tag instanceof FloatBinaryTag) {
                jsonObject.addProperty(key, ((FloatBinaryTag) tag).floatValue());
            } else if (tag instanceof IntArrayBinaryTag) {
                JsonArray jsonArray = new JsonArray();
                for (Integer value : ((IntArrayBinaryTag) tag)) {
                    jsonArray.add(value);
                }
                
                jsonObject.add(key, jsonArray);
            } else if (tag instanceof IntBinaryTag) {
                jsonObject.addProperty(key, ((IntBinaryTag) tag).intValue());
            } else if (tag instanceof LongArrayBinaryTag) {
                JsonArray jsonArray = new JsonArray();
                for (Long value : ((LongArrayBinaryTag) tag)) {
                    jsonArray.add(value);
                }
                
                jsonObject.add(key, jsonArray);
            } else if (tag instanceof LongBinaryTag) {
                jsonObject.addProperty(key, ((LongBinaryTag) tag).longValue());
            } else if (tag instanceof ShortBinaryTag) {
                jsonObject.addProperty(key, ((ShortBinaryTag) tag).shortValue());
            } else if (tag instanceof StringBinaryTag) {
                jsonObject.addProperty(key, ((StringBinaryTag) tag).value());
            }
        }
        
        return jsonObject;
    }
}