package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SlashReplyHandler implements ReplyHandler {
    private final SlashCommandEvent internalEvent;
    public SlashReplyHandler(SlashCommandEvent internalEvent) {
        this.internalEvent = internalEvent;
    }
    
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
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void reply(EmbedBuilder builder) {
        internalEvent.replyEmbeds(builder.build()).queue();
    }
    
    @Override
    public void replyFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        replyFile(preset.getEmbed(), file, name, options);
    }
    
    @Override
    public void replyFile(EmbedBuilder embed, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        internalEvent.replyEmbeds(embed.build()).addFile(file, name, options).queue();
    }
    
    @Override
    public void replyFile(String content, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        internalEvent.reply(content).addFile(file, name, options).queue();
    }
    
    @Deprecated
    @Override
    public void reply(EmbedBuilder builder, MessageChannel channel) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public MessageAction replyA(PresetBuilder preset) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public MessageAction replyA(PresetBuilder preset, MessageChannel channel) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public MessageAction embedReply(EmbedBuilder embed, MessageChannel channel) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public MessageAction textReply(String msg, MessageChannel channel) {
        throw new UnsupportedOperationException();
    }
}
