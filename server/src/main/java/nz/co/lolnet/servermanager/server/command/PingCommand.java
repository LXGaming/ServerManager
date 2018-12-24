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

package nz.co.lolnet.servermanager.server.command;

import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.PingPacket;
import nz.co.lolnet.servermanager.server.ServerManagerImpl;

import java.util.List;

public class PingCommand extends AbstractCommand {
    
    public PingCommand() {
        addAlias("ping");
        setDescription("Ping targeted server");
    }
    
    @Override
    public void execute(List<String> arguments) {
        if (arguments.isEmpty()) {
            ServerManager.getInstance().getLogger().info("Not enough arguments");
            return;
        }
        
        String channel = arguments.remove(0);
        PingPacket packet = new PingPacket(System.currentTimeMillis());
        packet.setType(Packet.Type.REQUEST);
        ServerManagerImpl.getInstance().sendPacket(channel, packet);
        ServerManager.getInstance().getLogger().info("Sending ping...");
    }
}