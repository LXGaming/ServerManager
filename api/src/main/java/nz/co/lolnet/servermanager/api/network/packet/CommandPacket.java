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

package nz.co.lolnet.servermanager.api.network.packet;

import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.NetworkHandler;

public class CommandPacket extends AbstractPacket {
    
    private final String command;
    private final User user;
    
    private CommandPacket(String command, User user) {
        this.command = command;
        this.user = user;
    }
    
    @Override
    public void process(NetworkHandler networkHandler) {
        networkHandler.handleCommand(this);
    }
    
    public static CommandPacket of(String command, User user) {
        return new CommandPacket(command, user);
    }
    
    public String getCommand() {
        return command;
    }
    
    public User getUser() {
        return user;
    }
}