/*
 * Copyright 2019 lolnet.co.nz
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

package nz.co.lolnet.servermanager.sponge.service;

import net.minecraft.server.MinecraftServer;
import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.common.service.AbstractService;
import nz.co.lolnet.servermanager.sponge.ServerManagerImpl;
import nz.co.lolnet.servermanager.sponge.SpongePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.common.SpongeImpl;

public class SpongeService extends AbstractService {
    
    private Platform.State lastState;
    
    @Override
    public boolean prepare() {
        setInterval(10000L);
        return true;
    }
    
    @Override
    public void execute() {
        if (SpongePlugin.getInstance() == null) {
            return;
        }
        
        Platform.State state = SpongePlugin.getInstance().getState();
        if (state == Platform.State.SERVER_STARTED || state == Platform.State.FROZEN) {
            if (isFrozen()) {
                updateState(Platform.State.FROZEN);
            } else {
                updateState(Platform.State.SERVER_STARTED);
            }
        }
    }
    
    private boolean isFrozen() {
        if (ServerManagerImpl.getInstance().getSetting() == null || !SpongeImpl.isInitialized() || !Sponge.isServerAvailable()) {
            return false;
        }
        
        Long tickTime = ServerManagerImpl.getInstance().getSetting().getMaxTickTime();
        if (tickTime == null) {
            return false;
        }
        
        return (System.currentTimeMillis() - ((MinecraftServer) Sponge.getServer()).getCurrentTime()) >= tickTime;
    }
    
    private void updateState(Platform.State state) {
        if (lastState == state) {
            return;
        }
        
        lastState = state;
        StatePacket packet = new StatePacket();
        packet.setState(lastState);
        ServerManager.getInstance().sendResponse(packet);
    }
}