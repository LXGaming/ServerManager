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
    
    @Listener
    public void onGameConstruction(GameConstructionEvent event) {
        instance = this;
        ServerManagerImpl.init();
    }
    
    @Listener
    public void onGameState(GameStateEvent event) {
        StatePacket packet = new StatePacket();
        packet.setState(getState());
        ServerManager.getInstance().sendResponse(packet);
    }
    
    public State getState() {
        if (Sponge.getGame().getState() == GameState.CONSTRUCTION) {
            return State.CONSTRUCTION;
        } else if (Sponge.getGame().getState() == GameState.PRE_INITIALIZATION) {
            return State.PRE_INITIALIZATION;
        } else if (Sponge.getGame().getState() == GameState.INITIALIZATION) {
            return State.INITIALIZATION;
        } else if (Sponge.getGame().getState() == GameState.POST_INITIALIZATION) {
            return State.POST_INITIALIZATION;
        } else if (Sponge.getGame().getState() == GameState.LOAD_COMPLETE) {
            return State.LOAD_COMPLETE;
        } else if (Sponge.getGame().getState() == GameState.SERVER_ABOUT_TO_START) {
            return State.SERVER_ABOUT_TO_START;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STARTING) {
            return State.SERVER_STARTING;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STARTED) {
            return State.SERVER_STARTED;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STOPPING) {
            return State.SERVER_STOPPING;
        } else if (Sponge.getGame().getState() == GameState.SERVER_STOPPED) {
            return State.SERVER_STOPPED;
        } else if (Sponge.getGame().getState() == GameState.GAME_STOPPING) {
            return State.GAME_STOPPING;
        } else if (Sponge.getGame().getState() == GameState.GAME_STOPPED) {
            return State.GAME_STOPPED;
        } else {
            return State.UNKNOWN;
        }
    }
    
    public static SpongePlugin getInstance() {
        return instance;
    }
    
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }
}