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

package io.github.lxgaming.servermanager.common.util.binary;

import io.github.lxgaming.binary.serializer.msgpack.MessagePackSerializer;
import io.github.lxgaming.binary.tag.Tag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;

public class MessagePackSerializerImpl extends MessagePackSerializer {
    
    public static final MessagePackSerializerImpl INSTANCE = new MessagePackSerializerImpl();
    
    private MessagePackSerializerImpl() {
    }
    
    public @NonNull Tag read(@NonNull ByteBuf byteBuf) throws DecoderException {
        try (MessageUnpacker unpacker = unpackerConfig.newUnpacker(byteBuf.nioBuffer())) {
            Tag tag = read(unpacker);
            byteBuf.readerIndex(byteBuf.readerIndex() + Math.toIntExact(unpacker.getTotalReadBytes()));
            return tag;
        } catch (IOException ex) {
            throw new DecoderException(ex);
        }
    }
    
    public void write(@NonNull ByteBuf byteBuf, @NonNull Tag tag) throws EncoderException {
        try (ByteBufOutputStream output = new ByteBufOutputStream(byteBuf)) {
            write(output, tag);
        } catch (IOException ex) {
            throw new EncoderException(ex);
        }
    }
}