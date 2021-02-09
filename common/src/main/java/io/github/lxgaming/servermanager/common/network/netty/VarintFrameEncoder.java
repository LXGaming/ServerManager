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

import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class VarintFrameEncoder extends MessageToByteEncoder<ByteBuf> {
    
    public static final VarintFrameEncoder INSTANCE = new VarintFrameEncoder();
    public static final String NAME = "frame-encoder";
    
    private VarintFrameEncoder() {
    }
    
    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        int capacity = 5 + msg.readableBytes();
        return preferDirect ? ctx.alloc().directBuffer(capacity) : ctx.alloc().heapBuffer(capacity);
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        ProtocolUtils.writeVarInt(out, msg.readableBytes());
        out.writeBytes(msg);
    }
}