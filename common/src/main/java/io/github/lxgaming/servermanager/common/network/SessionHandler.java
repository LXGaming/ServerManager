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

package io.github.lxgaming.servermanager.common.network;

import io.github.lxgaming.servermanager.common.network.packet.DisconnectPacket;
import io.github.lxgaming.servermanager.common.network.packet.HandshakePacket;
import io.github.lxgaming.servermanager.common.network.packet.HeartbeatPacket;
import io.github.lxgaming.servermanager.common.network.packet.HelloPacket;
import io.github.lxgaming.servermanager.common.network.packet.LoginPacket;
import io.github.lxgaming.servermanager.common.network.packet.StatusPacket;
import io.netty.buffer.ByteBuf;

public interface SessionHandler {
    
    default void activated() {
    }
    
    default void deactivated() {
    }
    
    default void exception(Throwable throwable) {
    }
    
    default void connected() {
    }
    
    default void disconnected() {
    }
    
    default boolean beforeHandle() {
        return false;
    }
    
    default boolean handle(DisconnectPacket packet) {
        return false;
    }
    
    default boolean handle(HandshakePacket packet) {
        return false;
    }
    
    default boolean handle(HeartbeatPacket packet) {
        return false;
    }
    
    default boolean handle(HelloPacket packet) {
        return false;
    }
    
    default boolean handle(LoginPacket.Request packet) {
        return false;
    }
    
    default boolean handle(LoginPacket.Response packet) {
        return false;
    }
    
    default boolean handle(StatusPacket packet) {
        return false;
    }
    
    default void handleGeneric(Packet packet) {
    }
    
    default void handleUnknown(ByteBuf byteBuf) {
    }
}