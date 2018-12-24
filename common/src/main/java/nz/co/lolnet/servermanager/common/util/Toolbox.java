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

package nz.co.lolnet.servermanager.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.util.Reference;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Toolbox {
    
    public static String getAddress(SocketAddress socketAddress) {
        return getHost(socketAddress).orElse("Unknown") + ":" + getPort(socketAddress).orElse(0);
    }
    
    public static Optional<String> getHost(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            return Optional.of(inetSocketAddress.getHostString());
        }
        
        return Optional.empty();
    }
    
    public static Optional<Integer> getPort(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            return Optional.of(inetSocketAddress.getPort());
        }
        
        return Optional.empty();
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
    
    public static String createChannel(Platform.Type platformType, String name) {
        if (Toolbox.isNotBlank(name)) {
            return Reference.ID + "-" + platformType + "-" + name.toLowerCase();
        }
        
        return null;
    }
    
    public static String createName(Platform.Type platformType, String name) {
        if (Toolbox.isNotBlank(name)) {
            return platformType.getFriendlyName() + name;
        }
        
        return null;
    }
    
    public static String getTimeString(long time) {
        time = Math.abs(time);
        long second = time / 1000;
        long minute = second / 60;
        long hour = minute / 60;
        long day = hour / 24;
        
        StringBuilder stringBuilder = new StringBuilder();
        appendUnit(stringBuilder, day, "day", "days");
        appendUnit(stringBuilder, hour % 24, "hour", "hours");
        appendUnit(stringBuilder, minute % 60, "minute", "minutes");
        appendUnit(stringBuilder, second % 60, "second", "seconds");
        
        if (stringBuilder.length() == 0) {
            stringBuilder.append("just now");
        }
        
        return stringBuilder.toString();
    }
    
    public static void appendUnit(StringBuilder stringBuilder, long unit, String singular, String plural) {
        if (unit > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            
            stringBuilder.append(unit).append(" ");
            if (unit == 1) {
                stringBuilder.append(singular);
            } else {
                stringBuilder.append(plural);
            }
        }
    }
    
    public static <T> Optional<T> parseJson(String json, Class<T> type) {
        try {
            return parseJson(new JsonParser().parse(json), type);
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
    
    public static <T> Optional<T> parseJson(JsonElement jsonElement, Class<T> type) {
        try {
            return Optional.of(new Gson().fromJson(jsonElement, type));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
    
    public static boolean isBlank(CharSequence charSequence) {
        int stringLength;
        if (charSequence == null || (stringLength = charSequence.length()) == 0) {
            return true;
        }
        
        for (int index = 0; index < stringLength; index++) {
            if (!Character.isWhitespace(charSequence.charAt(index))) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean isNotBlank(CharSequence charSequence) {
        return !isBlank(charSequence);
    }
    
    public static boolean containsIgnoreCase(Collection<String> list, String targetString) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        
        for (String string : list) {
            if (string.equalsIgnoreCase(targetString)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static <T> T cast(Object object, Class<T> type) {
        return type.cast(object);
    }
    
    public static <T> Optional<T> newInstance(Class<? extends T> typeOfT) {
        try {
            return Optional.of(typeOfT.newInstance());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    
    public static ThreadFactory buildThreadFactory(String namingPattern) {
        return new ThreadFactoryBuilder().daemon(true).namingPattern(namingPattern).priority(Thread.NORM_PRIORITY).build();
    }
    
    public static Optional<Path> getPath() {
        String userDir = System.getProperty("user.dir");
        if (isNotBlank(userDir)) {
            return Optional.of(Paths.get(userDir));
        }
        
        return Optional.empty();
    }
    
    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(ArrayList::new));
    }
    
    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(HashSet::new));
    }
    
    @SafeVarargs
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(LinkedBlockingQueue::new));
    }
    
    @SafeVarargs
    public static <E> LinkedHashSet<E> newLinkedHashSet(E... elements) {
        return Stream.of(elements).collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }
}