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

import io.github.lxgaming.servermanager.common.network.Direction;
import io.github.lxgaming.servermanager.common.network.Packet;
import io.github.lxgaming.servermanager.common.network.StateRegistry;
import io.github.lxgaming.servermanager.common.network.util.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;

public class PacketDecoder extends ChannelInboundHandlerAdapter {
    
    public static final String NAME = "packet-decoder";
    
    private final Direction direction;
    private StateRegistry state;
    
    public PacketDecoder(Direction direction) {
        this.direction = direction;
        this.state = StateRegistry.HANDSHAKE;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            tryDecode(ctx, byteBuf);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
    
    private void tryDecode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        if (!ctx.channel().isActive()) {
            byteBuf.release();
            return;
        }
        
        int originalReaderIndex = byteBuf.readerIndex();
        int packetId = ProtocolUtils.readVarInt(byteBuf);
        Packet packet = state.createPacket(direction, packetId);
        if (packet == null) {
            byteBuf.readerIndex(originalReaderIndex);
            ctx.fireChannelRead(byteBuf);
            return;
        }
        
        try {
            try {
                packet.decode(byteBuf);
            } catch (Exception ex) {
                throw new CorruptedFrameException("Error decoding " + packet.getClass().getName() + " "
                        + "(Direction " + direction + ", State " + state + ", Id " + Integer.toHexString(packetId) + ")", ex);
            }
            
            if (byteBuf.isReadable()) {
                throw new CorruptedFrameException("Packet sent for " + packet.getClass().getName() + " was too big "
                        + "(expected " + byteBuf.readerIndex() + " bytes, got " + byteBuf.writerIndex() + " bytes)");
            }
            
            ctx.fireChannelRead(packet);
        } finally {
            byteBuf.release();
        }
    }
    
    public void setState(StateRegistry state) {
        this.state = state;
    }
}