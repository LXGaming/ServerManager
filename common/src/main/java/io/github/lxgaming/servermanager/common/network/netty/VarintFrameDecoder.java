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

package io.github.lxgaming.servermanager.common.network.netty;

import io.github.lxgaming.servermanager.common.exception.QuietException;
import io.github.lxgaming.servermanager.common.network.netty.VarintByteDecoder.DecodeResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class VarintFrameDecoder extends ByteToMessageDecoder {
    
    public static final String NAME = "frame-decoder";
    private static final QuietException BAD_LENGTH_CACHED = new QuietException("Bad packet length");
    private static final QuietException VARINT_BIG_CACHED = new QuietException("VarInt too big");
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        }
        
        VarintByteDecoder reader = new VarintByteDecoder();
        
        int varintEnd = in.forEachByte(reader);
        if (varintEnd == -1) {
            return;
        }
        
        if (reader.getResult() == DecodeResult.SUCCESS) {
            int readVarint = reader.getReadVarint();
            int bytesRead = reader.getBytesRead();
            
            if (readVarint < 0) {
                throw BAD_LENGTH_CACHED;
            } else if (readVarint == 0) {
                in.readerIndex(varintEnd + 1);
            } else {
                int minimumRead = bytesRead + readVarint;
                if (in.isReadable(minimumRead)) {
                    out.add(in.retainedSlice(varintEnd + 1, readVarint));
                    in.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == DecodeResult.TOO_BIG) {
            throw VARINT_BIG_CACHED;
        }
    }
}