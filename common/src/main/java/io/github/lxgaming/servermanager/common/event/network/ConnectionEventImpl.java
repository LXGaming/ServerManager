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

package io.github.lxgaming.servermanager.common.event.network;

import io.github.lxgaming.servermanager.api.entity.Platform;
import io.github.lxgaming.servermanager.api.event.network.ConnectionEvent;
import io.github.lxgaming.servermanager.common.entity.Connection;
import io.github.lxgaming.servermanager.common.event.EventImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.InetSocketAddress;

public class ConnectionEventImpl extends EventImpl implements ConnectionEvent {
    
    private final Connection connection;
    
    private ConnectionEventImpl(@NonNull Platform platform, @NonNull Connection connection) {
        super(platform);
        this.connection = connection;
    }
    
    @Override
    public final @NonNull InetSocketAddress getAddress() {
        return getConnection().getAddress();
    }
    
    public final @NonNull Connection getConnection() {
        return connection;
    }
    
    public static final class Connect extends ConnectionEventImpl implements ConnectionEvent.Connect {
        
        public Connect(@NonNull Platform platform, @NonNull Connection connection) {
            super(platform, connection);
        }
    }
    
    public static final class Handshake extends ConnectionEventImpl implements ConnectionEvent.Handshake {
        
        public Handshake(@NonNull Platform platform, @NonNull Connection connection) {
            super(platform, connection);
        }
    }
    
    public static final class Status extends ConnectionEventImpl implements ConnectionEvent.Status {
        
        public Status(@NonNull Platform platform, @NonNull Connection connection) {
            super(platform, connection);
        }
    }
    
    public static final class Login extends ConnectionEventImpl implements ConnectionEvent.Login {
        
        public Login(@NonNull Platform platform, @NonNull Connection connection) {
            super(platform, connection);
        }
    }
    
    public static final class Instance extends ConnectionEventImpl implements ConnectionEvent.Instance {
        
        public Instance(@NonNull Platform platform, @NonNull Connection connection) {
            super(platform, connection);
        }
    }
    
    public static final class Disconnect extends ConnectionEventImpl implements ConnectionEvent.Disconnect {
        
        public Disconnect(@NonNull Platform platform, @NonNull Connection connection) {
            super(platform, connection);
        }
    }
}