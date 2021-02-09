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

package io.github.lxgaming.servermanager.common.configuration.category;

import com.google.gson.annotations.SerializedName;

public class NetworkCategory {
    
    public static final int DEFAULT_MAXIMUM_THREADS = 0;
    public static final int DEFAULT_PORT = 5976; // LXSM - LX ServerManager
    public static final int DEFAULT_READ_TIMEOUT = 30000; // 30 Seconds
    
    @SerializedName("host")
    private String host = "127.0.0.1";
    
    @SerializedName("port")
    private int port = DEFAULT_PORT;
    
    @SerializedName("maximumThreads")
    private int maximumThreads = DEFAULT_MAXIMUM_THREADS;
    
    @SerializedName("nativeTransport")
    private boolean nativeTransport = true;
    
    @SerializedName("readTimeout")
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getMaximumThreads() {
        return maximumThreads;
    }
    
    public void setMaximumThreads(int maximumThreads) {
        this.maximumThreads = maximumThreads;
    }
    
    public boolean isNativeTransport() {
        return nativeTransport;
    }
    
    public void setNativeTransport(boolean nativeTransport) {
        this.nativeTransport = nativeTransport;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}