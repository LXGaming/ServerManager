/*
 * Copyright 2018 Alex Thomson
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

package io.github.lxgaming.servermanager.sponge.util;

import com.google.common.collect.ImmutableList;
import io.github.lxgaming.servermanager.api.util.Reference;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SpongeCommandSource implements CommandSource {
    
    @Override
    public String getName() {
        return Reference.NAME;
    }
    
    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(this);
    }
    
    @Override
    public SubjectCollection getContainingCollection() {
        return null;
    }
    
    @Override
    public SubjectReference asSubjectReference() {
        return null;
    }
    
    @Override
    public boolean isSubjectDataPersisted() {
        return false;
    }
    
    @Override
    public SubjectData getSubjectData() {
        return null;
    }
    
    @Override
    public SubjectData getTransientSubjectData() {
        return null;
    }
    
    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return Tristate.TRUE;
    }
    
    @Override
    public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
        return false;
    }
    
    @Override
    public List<SubjectReference> getParents(Set<Context> contexts) {
        return ImmutableList.of();
    }
    
    @Override
    public Optional<String> getOption(Set<Context> contexts, String key) {
        return Optional.empty();
    }
    
    @Override
    public String getIdentifier() {
        return Reference.ID;
    }
    
    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }
    
    @Override
    public void sendMessage(Text message) {
    
    }
    
    @Override
    public MessageChannel getMessageChannel() {
        return MessageChannel.TO_CONSOLE;
    }
    
    @Override
    public void setMessageChannel(MessageChannel channel) {
    
    }
    
    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }
    
    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return true;
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
    
    @Override
    public boolean isChildOf(SubjectReference parent) {
        return false;
    }
    
    @Override
    public List<SubjectReference> getParents() {
        return ImmutableList.of();
    }
    
    @Override
    public Optional<String> getOption(String key) {
        return Optional.empty();
    }
    
    @Override
    public Optional<String> getFriendlyIdentifier() {
        return Optional.empty();
    }
    
    @Override
    public void sendMessage(TextTemplate template) {
    
    }
    
    @Override
    public void sendMessage(TextTemplate template, Map<String, TextElement> parameters) {
    
    }
    
    @Override
    public void sendMessages(Text... messages) {
    
    }
    
    @Override
    public void sendMessages(Iterable<Text> messages) {
    
    }
}
