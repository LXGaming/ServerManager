/*
 * Copyright 2019 Alex Thomson
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

package io.github.lxgaming.servermanager.velocity.util;

import com.velocitypowered.api.util.MessagePosition;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import io.github.lxgaming.servermanager.api.network.packet.MessagePacket;
import io.github.lxgaming.servermanager.api.util.Reference;

public class VelocityToolbox {
    
    public static TextComponent getTextPrefix() {
        TextComponent.Builder textBuilder = TextComponent.builder();
        textBuilder.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, getPluginInformation()));
        textBuilder.content("[" + Reference.NAME + "]").color(TextColor.BLUE).decoration(TextDecoration.BOLD, true);
        return TextComponent.of("").append(textBuilder.build()).append(TextComponent.of(" "));
    }
    
    public static TextComponent getPluginInformation() {
        TextComponent.Builder textBuilder = TextComponent.builder("");
        textBuilder.append(TextComponent.of(Reference.NAME, TextColor.BLUE).decoration(TextDecoration.BOLD, true)).append(TextComponent.newline());
        textBuilder.append(TextComponent.of("    Version: ", TextColor.DARK_GRAY)).append(TextComponent.of(Reference.VERSION, TextColor.WHITE)).append(TextComponent.newline());
        textBuilder.append(TextComponent.of("    Authors: ", TextColor.DARK_GRAY)).append(TextComponent.of(Reference.AUTHORS, TextColor.WHITE)).append(TextComponent.newline());
        textBuilder.append(TextComponent.of("    Source: ", TextColor.DARK_GRAY)).append(getURLTextAction(Reference.SOURCE)).append(TextComponent.newline());
        textBuilder.append(TextComponent.of("    Website: ", TextColor.DARK_GRAY)).append(getURLTextAction(Reference.WEBSITE));
        return textBuilder.build();
    }
    
    public static TextComponent getURLTextAction(String url) {
        TextComponent.Builder textBuilder = TextComponent.builder();
        textBuilder.clickEvent(ClickEvent.of(ClickEvent.Action.OPEN_URL, url));
        textBuilder.content(url).color(TextColor.BLUE);
        return textBuilder.build();
    }
    
    public static MessagePosition getMessagePosition(MessagePacket.Position position) {
        if (position == MessagePacket.Position.ACTION_BAR) {
            return MessagePosition.ACTION_BAR;
        }
        
        return MessagePosition.CHAT;
    }
    
    @SuppressWarnings("deprecation")
    public static TextComponent deserializeLegacy(String string) {
        return LegacyComponentSerializer.INSTANCE.deserialize(string, '&');
    }
}