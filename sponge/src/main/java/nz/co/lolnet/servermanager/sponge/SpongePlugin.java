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
import nz.co.lolnet.servermanager.api.data.Platform;
import nz.co.lolnet.servermanager.api.network.packet.StatePacket;
import nz.co.lolnet.servermanager.api.util.Logger;
import nz.co.lolnet.servermanager.api.util.Reference;
import nz.co.lolnet.servermanager.sponge.listener.SpongeListener;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

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
    
    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path path;
    
    @Listener
    public void onGameConstruction(GameConstructionEvent event) {
        instance = this;
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger()
                .add(Logger.Level.INFO, getPluginContainer().getLogger()::info)
                .add(Logger.Level.WARN, getPluginContainer().getLogger()::warn)
                .add(Logger.Level.ERROR, getPluginContainer().getLogger()::error)
                .add(Logger.Level.DEBUG, getPluginContainer().getLogger()::debug);
        
        serverManager.loadServerManager();
        serverManager.reloadServerManager();
    }
    
    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(getPluginContainer(), new SpongeListener());
    }
    
    @Listener
    public void onGameState(GameStateEvent event) {
        if (event.getState() == GameState.CONSTRUCTION) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.CONSTRUCTION));
        } else if (event.getState() == GameState.PRE_INITIALIZATION) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.PRE_INITIALIZATION));
        } else if (event.getState() == GameState.INITIALIZATION) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.INITIALIZATION));
        } else if (event.getState() == GameState.POST_INITIALIZATION) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.POST_INITIALIZATION));
        } else if (event.getState() == GameState.LOAD_COMPLETE) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.LOAD_COMPLETE));
        } else if (event.getState() == GameState.SERVER_ABOUT_TO_START) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.SERVER_ABOUT_TO_START));
        } else if (event.getState() == GameState.SERVER_STARTING) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.SERVER_STARTING));
        } else if (event.getState() == GameState.SERVER_STARTED) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.SERVER_STARTED));
        } else if (event.getState() == GameState.SERVER_STOPPING) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.SERVER_STOPPING));
        } else if (event.getState() == GameState.SERVER_STOPPED) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.SERVER_STOPPED));
        } else if (event.getState() == GameState.GAME_STOPPING) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.GAME_STOPPING));
        } else if (event.getState() == GameState.GAME_STOPPED) {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.GAME_STOPPED));
        } else {
            ServerManagerImpl.getInstance().getRedisService().publish(StatePacket.of(State.UNKNOWN));
        }
    }
    
    public Path getPath() {
        return path;
    }
    
    public static SpongePlugin getInstance() {
        return instance;
    }
    
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }
}