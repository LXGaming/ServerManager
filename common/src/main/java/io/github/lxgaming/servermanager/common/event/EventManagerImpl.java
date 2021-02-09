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

package io.github.lxgaming.servermanager.common.event;

import com.google.common.base.Preconditions;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.event.EventManager;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import net.kyori.event.EventBus;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import net.kyori.event.method.asm.ASMEventExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManagerImpl implements EventManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.NAME);
    
    private final EventBus<Object> eventBus;
    private final MethodSubscriptionAdapter<Object> methodSubscriptionAdapter;
    private final ExecutorService executorService;
    
    public EventManagerImpl() {
        this.eventBus = new SimpleEventBus<>(Object.class);
        this.methodSubscriptionAdapter = new SimpleMethodSubscriptionAdapter<>(eventBus, new ASMEventExecutorFactory<>());
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Toolbox.newThreadFactory("Event Executor - #%d"));
    }
    
    @Override
    public <T> CompletableFuture<T> fire(T event) {
        Preconditions.checkNotNull(event, "event");
        if (!eventBus.hasSubscribers(event.getClass())) {
            return CompletableFuture.completedFuture(event);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            fireEvent(event);
            return event;
        }, executorService);
    }
    
    @Override
    public void fireAndForget(Object event) {
        Preconditions.checkNotNull(event, "event");
        if (!eventBus.hasSubscribers(event.getClass())) {
            return;
        }
        
        executorService.execute(() -> fireEvent(event));
    }
    
    @Override
    public void register(Object listener) {
        Preconditions.checkNotNull(listener, "listener");
        methodSubscriptionAdapter.register(listener);
    }
    
    @Override
    public void unregister(Object listener) {
        Preconditions.checkNotNull(listener, "listener");
        methodSubscriptionAdapter.unregister(listener);
    }
    
    private void fireEvent(Object event) {
        PostResult result = eventBus.post(event);
        if (!result.exceptions().isEmpty()) {
            LOGGER.error("Some errors occurred whilst posting event {}.", event);
            int index = 0;
            for (Throwable throwable : result.exceptions().values()) {
                LOGGER.error("#{}: \n", ++index, throwable);
            }
        }
    }
}