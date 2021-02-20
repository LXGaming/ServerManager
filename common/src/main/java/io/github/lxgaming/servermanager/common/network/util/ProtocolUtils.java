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

package io.github.lxgaming.servermanager.common.network.util;

import com.google.common.base.Preconditions;
import io.github.lxgaming.binary.tag.Tag;
import io.github.lxgaming.servermanager.common.util.binary.MessagePackSerializerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ProtocolUtils {
    
    private static final int DEFAULT_MAX_LENGTH = 65536; // 64KiB
    
    public static int readVarInt(ByteBuf byteBuf) {
        int read = readVarIntSafely(byteBuf);
        if (read == Integer.MIN_VALUE) {
            throw new IllegalStateException("VarInt too big");
        }
        
        return read;
    }
    
    public static int readVarIntSafely(ByteBuf buf) {
        int value = 0;
        int maxLength = Math.min(5, buf.readableBytes());
        for (int length = 0; length < maxLength; length++) {
            int read = buf.readByte();
            value |= (read & 0x7F) << length * 7;
            if ((read & 0x80) != 128) {
                return value;
            }
        }
        
        return Integer.MIN_VALUE;
    }
    
    public static void writeVarInt(ByteBuf byteBuf, int value) {
        while (true) {
            if ((value & 0xFFFFFF80) == 0) {
                byteBuf.writeByte(value);
                return;
            }
            
            byteBuf.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }
    
    public static String readString(ByteBuf byteBuf) {
        return readString(byteBuf, DEFAULT_MAX_LENGTH);
    }
    
    public static String readString(ByteBuf byteBuf, int maxLength) {
        int length = readVarInt(byteBuf);
        return readString(byteBuf, length, maxLength);
    }
    
    public static String readString(ByteBuf byteBuf, int length, int maxLength) {
        Preconditions.checkArgument(length >= 0, "Got a negative-length string (%s)", length);
        Preconditions.checkArgument(length <= maxLength * 4, "Bad string size (got %s, maximum is %s)", length, maxLength);
        Preconditions.checkState(byteBuf.isReadable(length), "Trying to read a string that is too long (wanted %s, only have %s)", length, byteBuf.readableBytes());
        String string = byteBuf.toString(byteBuf.readerIndex(), length, StandardCharsets.UTF_8);
        byteBuf.skipBytes(length);
        Preconditions.checkState(string.length() <= maxLength, "Got a too-long string (got %s, max %s)", string.length(), maxLength);
        return string;
    }
    
    public static void writeString(ByteBuf byteBuf, CharSequence charSequence) {
        int length = ByteBufUtil.utf8Bytes(charSequence);
        writeVarInt(byteBuf, length);
        byteBuf.writeCharSequence(charSequence, StandardCharsets.UTF_8);
    }
    
    public static Tag readTag(ByteBuf byteBuf) {
        return MessagePackSerializerImpl.INSTANCE.read(byteBuf);
    }
    
    public static void writeTag(ByteBuf byteBuf, Tag tag) {
        MessagePackSerializerImpl.INSTANCE.write(byteBuf, tag);
    }
    
    public static UUID readUUID(ByteBuf byteBuf) {
        long mostSigBits = byteBuf.readLong();
        long leastSigBits = byteBuf.readLong();
        return new UUID(mostSigBits, leastSigBits);
    }
    
    public static void writeUUID(ByteBuf byteBuf, UUID uuid) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }
    
    public static long[] readLongArray(ByteBuf byteBuf) {
        return readLongArray(byteBuf, DEFAULT_MAX_LENGTH);
    }
    
    public static long[] readLongArray(ByteBuf byteBuf, int maxLength) {
        int length = readVarInt(byteBuf);
        Preconditions.checkArgument(length >= 0, "Got a negative-length array (%s)", length);
        Preconditions.checkArgument(length <= maxLength, "Bad array size (got %s, maximum is %s)", length, maxLength);
        Preconditions.checkState(byteBuf.isReadable(length), "Trying to read an array that is too long (wanted %s, only have %s)", length, byteBuf.readableBytes());
        long[] array = new long[length];
        for (int index = 0; index < length; index++) {
            array[index] = byteBuf.readLong();
        }
        
        return array;
    }
    
    public static void writeLongArray(ByteBuf byteBuf, long[] array) {
        writeVarInt(byteBuf, array.length);
        for (long value : array) {
            byteBuf.writeLong(value);
        }
    }
    
    public static String[] readStringArray(ByteBuf byteBuf) {
        return readStringArray(byteBuf, DEFAULT_MAX_LENGTH, DEFAULT_MAX_LENGTH);
    }
    
    public static String[] readStringArray(ByteBuf byteBuf, int maxArrayLength, int maxStringLength) {
        int length = readVarInt(byteBuf);
        Preconditions.checkArgument(length >= 0, "Got a negative-length array (%s)", length);
        Preconditions.checkArgument(length <= maxArrayLength, "Bad array size (got %s, maximum is %s)", length, maxArrayLength);
        Preconditions.checkState(byteBuf.isReadable(length), "Trying to read an array that is too long (wanted %s, only have %s)", length, byteBuf.readableBytes());
        String[] array = new String[length];
        for (int index = 0; index < length; index++) {
            array[index] = readString(byteBuf, maxStringLength);
        }
        
        return array;
    }
    
    public static void writeStringArray(ByteBuf byteBuf, String[] array) {
        writeVarInt(byteBuf, array.length);
        for (String value : array) {
            writeString(byteBuf, value);
        }
    }
}