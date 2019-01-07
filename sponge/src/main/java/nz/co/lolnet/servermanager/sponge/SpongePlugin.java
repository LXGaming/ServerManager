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

package nz.co.lolnet.servermanager.sponge;

import com.google.inject.Inject;
import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.ServerManager;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.util.Reference;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
        id = Reference.ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        description = Reference.DESCRIPTION,
        authors = {Reference.AUTHORS},
        url = Reference.WEBSITE
)
public class SpongePlugin implements Platform {
    
    private static SpongePlugin instance;
    
    @Inject
    private PluginContainer pluginContainer;
    
    @Listener(order = Order.EARLY)
    public void onGameConstruction(GameConstructionEvent event) {
        instance = this;
        ServerManagerImpl.init();
    }
    
    @Listener
    public void onGameState(GameStateEvent event) {
        if (ServerManager.getInstance() == null) {
            return;
        }
        
        StatePacket packet = new StatePacket();
        packet.setState(getState());
        ServerManager.getInstance().sendResponse(packet);
    }
    
    public Platform.State getState() {
        if (Sponge.getGame().getState() == GameState.CONSTRUCTION) {
            return Platform.State.CONSTRUCTION;
        } else if (Sponge.getGame().getState() == GameState.PRE_INITIALIZATION) {
            return Platform.State.PRE_INITIALIZATION;
        } else if (Sponge.getGame().getState() == GameState.INITIALIZATION) {
            return Platform.State.INITIALIZATION;
        } else if (Sponge.getGame().getState() == GameState.POST_INITIALIZATION) {
            return Platform.State.POST_INITIALIZATION;
        } else if (Sponge.getGame().getState() == GameState.LOAD_COMPLETE) {
            return Platform.State.LOAD_COMPLETE;
        } else if (Sponge.getGame().getState() == GameState.SERVER_ABOUT_TO_START) {
            return Platform.State.SERVER_ABOUT_TO_START;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STARTING) {
            return Platform.State.SERVER_STARTING;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STARTED) {
            return Platform.State.SERVER_STARTED;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STOPPING) {
            return Platform.State.SERVER_STOPPING;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STOPPED
                || Sponge.getGame().getState() == GameState.GAME_STOPPING
                || Sponge.getGame().getState() == GameState.GAME_STOPPED) {
            return Platform.State.SERVER_STOPPED;
        } else {
            return Platform.State.UNKNOWN;
        }
    }
    
    public static SpongePlugin getInstance() {
        return instance;
    }
    
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }
}