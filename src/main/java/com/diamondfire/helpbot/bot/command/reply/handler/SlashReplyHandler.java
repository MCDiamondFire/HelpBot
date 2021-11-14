package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public record SlashReplyHandler(SlashCommandEvent internalEvent) implements ReplyHandler {
    @Override
    public void reply(String content) {
        internalEvent.reply(content).queue();
    }
    
    @Override
    public void reply(PresetBuilder preset) {
        reply(preset.getEmbed());
    }
    
    @Deprecated
    @Override
    public void reply(PresetBuilder preset, MessageChannel channel) {
    }
    
    @Override
    public void reply(EmbedBuilder builder) {
        internalEvent.replyEmbeds(builder.build()).queue();
    }
    
    @Override
    public void replyFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        internalEvent.replyEmbeds(preset.getEmbed().build()).addFile(file, name, options).queue();
    }
    
    @Deprecated
    @Override
    public void reply(EmbedBuilder builder, MessageChannel channel) {
    }
    
    @Override
    public MessageAction replyA(PresetBuilder preset) {
        return null;
    }
    
    @Deprecated
    @Override
    public MessageAction replyA(PresetBuilder preset, MessageChannel channel) {
        return null;
    }
    
    @Deprecated
    @Override
    public MessageAction embedReply(EmbedBuilder embed, MessageChannel channel) {
        return null;
    }
    
    @Deprecated
    @Override
    public MessageAction textReply(String msg, MessageChannel channel) {
        return null;
    }
}
