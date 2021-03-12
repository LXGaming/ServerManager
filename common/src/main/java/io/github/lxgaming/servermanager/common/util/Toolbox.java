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

package io.github.lxgaming.servermanager.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Toolbox {
    
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .enableComplexMapKeySerialization()
            .create();
    
    public static String getAddress(SocketAddress socketAddress) {
        return getHost(socketAddress) + ":" + getPort(socketAddress);
    }
    
    public static String getHost(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            return inetSocketAddress.getHostString();
        }
        
        return null;
    }
    
    public static Integer getPort(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            return inetSocketAddress.getPort();
        }
        
        return null;
    }
    
    public static boolean isPrivateAddress(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            return isPrivateAddress(((InetSocketAddress) socketAddress).getAddress());
        }
        
        return false;
    }
    
    public static boolean isPrivateAddress(InetAddress address) {
        return address.isLoopbackAddress() || address.isSiteLocalAddress();
    }
    
    /**
     * Removes non-printable characters (excluding new line and carriage return) in the provided {@link java.lang.String String}.
     *
     * @param string The {@link java.lang.String String} to filter.
     * @return The filtered {@link java.lang.String String}.
     */
    public static String filter(String string) {
        return string.replaceAll("[^\\x20-\\x7E\\x0A\\x0D]", "");
    }
    
    public static String getDuration(long value, TimeUnit unit, TimeUnit precision) {
        return getDuration(value, unit, false, precision);
    }
    
    public static String getDuration(long value, TimeUnit unit, boolean abbreviate, TimeUnit precision) {
        StringBuilder stringBuilder = new StringBuilder();
        append(stringBuilder, precision.convert(value, unit), precision, abbreviate);
        return stringBuilder.toString();
    }
    
    public static String getDuration(long value, TimeUnit unit) {
        return getDuration(value, unit, false);
    }
    
    public static String getDuration(long value, TimeUnit unit, boolean abbreviate) {
        StringBuilder stringBuilder = new StringBuilder();
        if (TimeUnit.DAYS.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toDays(value), TimeUnit.DAYS, abbreviate);
        }
        
        if (TimeUnit.HOURS.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toHours(value) % 24, TimeUnit.HOURS, abbreviate);
        }
        
        if (TimeUnit.MINUTES.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toMinutes(value) % 60, TimeUnit.MINUTES, abbreviate);
        }
        
        if (TimeUnit.SECONDS.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toSeconds(value) % 60, TimeUnit.SECONDS, abbreviate);
        }
        
        if (TimeUnit.MILLISECONDS.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toMillis(value) % 1000, TimeUnit.MILLISECONDS, abbreviate);
        }
        
        if (TimeUnit.MICROSECONDS.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toMicros(value) % 1000, TimeUnit.MICROSECONDS, abbreviate);
        }
        
        if (TimeUnit.NANOSECONDS.compareTo(unit) >= 0) {
            append(stringBuilder, unit.toNanos(value) % 1000, TimeUnit.NANOSECONDS, abbreviate);
        }
        
        return stringBuilder.toString();
    }
    
    public static void append(StringBuilder stringBuilder, long value, TimeUnit unit, boolean abbreviate) {
        if (value <= 0) {
            return;
        }
        
        if (stringBuilder.length() > 0) {
            stringBuilder.append(abbreviate ? " " : ", ");
        }
        
        stringBuilder.append(value);
        if (!abbreviate) {
            stringBuilder.append(" ");
        }
        
        switch (unit) {
            case NANOSECONDS:
                stringBuilder.append(abbreviate ? "ns" : value == 1 ? "nanosecond" : "nanoseconds");
                break;
            case MICROSECONDS:
                stringBuilder.append(abbreviate ? "\u03BCs" : value == 1 ? "microsecond" : "microseconds");
                break;
            case MILLISECONDS:
                stringBuilder.append(abbreviate ? "ms" : value == 1 ? "millisecond" : "milliseconds");
                break;
            case SECONDS:
                stringBuilder.append(abbreviate ? "s" : value == 1 ? "second" : "seconds");
                break;
            case MINUTES:
                stringBuilder.append(abbreviate ? "min" : value == 1 ? "minute" : "minutes");
                break;
            case HOURS:
                stringBuilder.append(abbreviate ? "h" : value == 1 ? "hour" : "hours");
                break;
            case DAYS:
                stringBuilder.append(abbreviate ? "d" : value == 1 ? "day" : "days");
                break;
        }
    }
    
    public static <T> T cast(Object object, Class<? extends T> type) {
        return type.cast(object);
    }
    
    public static String getClassSimpleName(Object object) {
        if (object != null) {
            return getClassSimpleName(object.getClass());
        }
        
        return null;
    }
    
    public static String getClassSimpleName(Class<?> type) {
        if (type.getEnclosingClass() != null) {
            return getClassSimpleName(type.getEnclosingClass()) + "." + type.getSimpleName();
        }
        
        return type.getSimpleName();
    }
    
    public static <T> T newInstance(Class<? extends T> type) {
        try {
            return type.newInstance();
        } catch (Throwable ex) {
            return null;
        }
    }
    
    public static Path getPath() {
        String userDir = System.getProperty("user.dir");
        if (StringUtils.isNotBlank(userDir)) {
            return Paths.get(userDir);
        }
        
        return Paths.get(".");
    }
    
    public static ThreadFactory newThreadFactory(String namingPattern) {
        return new ThreadFactoryBuilder().setNameFormat(namingPattern).setDaemon(true).setPriority(Thread.NORM_PRIORITY).build();
    }
}