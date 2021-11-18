package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MessageReplyHandler implements ReplyHandler {
    private final TextChannel channel;
    public MessageReplyHandler(TextChannel channel) {
        this.channel = channel;
    }
    
    public void reply(String content) {
        textReply(content, channel).queue();
    }
    
    public void reply(PresetBuilder preset) {
        reply(preset, channel);
    }
    
    public void reply(PresetBuilder preset, MessageChannel channel) {
        reply(preset.getEmbed(), channel);
    }
    
    public void reply(EmbedBuilder builder) {
        embedReply(builder, channel).queue();
    }
    
    public void reply(EmbedBuilder builder, MessageChannel channel) {
        embedReply(builder, channel).queue();
    }
    
    @Override
    public void replyFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        replyFile(preset.getEmbed(), file, name, options);
    }
    
    @Override
    public void replyFile(EmbedBuilder embed, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        channel.sendMessageEmbeds(embed.build()).addFile(file, name, options).queue();
    }
    
    @Override
    public void replyFile(String content, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        channel.sendMessage(content).addFile(file, name, options).queue();
    }
    
    public MessageAction replyA(PresetBuilder preset) {
        return replyA(preset, channel);
    }
    
    public MessageAction replyA(PresetBuilder preset, MessageChannel channel) {
        return embedReply(preset.getEmbed(), channel);
    }
    
    public MessageAction embedReply(EmbedBuilder embed, MessageChannel channel) {
        return channel.sendMessageEmbeds(embed.build());
    }
    
    public MessageAction textReply(String msg, MessageChannel channel) {
        return channel.sendMessage(msg);
    }

}
