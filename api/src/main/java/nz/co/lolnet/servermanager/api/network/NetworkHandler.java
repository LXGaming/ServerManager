/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.servermanager.api.network;

import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.api.network.packet.ListPacket;
import nz.co.lolnet.servermanager.api.network.packet.MessagePacket;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.api.network.packet.SettingPacket;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.network.packet.StatusPacket;
import nz.co.lolnet.servermanager.api.network.packet.UserPacket;

public interface NetworkHandler {
    
    boolean handle(Packet packet);
    
    void handleCommand(CommandPacket packet);
    
    void handleListBasic(ListPacket.Basic packet);
    
    void handleListFull(ListPacket.Full packet);
    
    void handleMessage(MessagePacket packet);
    
    void handlePing(PingPacket packet);
    
    void handleSetting(SettingPacket packet);
    
    void handleState(StatePacket packet);
    
    void handleStatus(StatusPacket packet);
    
    void handleUserAdd(UserPacket.Add packet);
    
    void handleUserRemove(UserPacket.Remove packet);
}