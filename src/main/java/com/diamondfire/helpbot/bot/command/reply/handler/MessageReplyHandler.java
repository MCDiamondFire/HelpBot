package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.followup.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class MessageReplyHandler implements ReplyHandler {
    
    private final TextChannel channel;
    
    public MessageReplyHandler(TextChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> reply(String content) {
        return channel.sendMessage(content)
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> reply(PresetBuilder preset) {
        return reply(preset.getEmbed());
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> reply(EmbedBuilder builder) {
        return channel.sendMessageEmbeds(builder.build())
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> replyFile(PresetBuilder preset, byte[] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return replyFile(preset.getEmbed(), data, name, options);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> replyFile(EmbedBuilder embed, byte[] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return channel.sendMessageEmbeds(embed.build())
                .addFile(data, name, options)
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> replyFile(String content, byte[] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return channel.sendMessage(content)
                .addFile(data, name, options)
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> deferReply() {
        return reply(String.format("*%s is thinking...*", HelpBotInstance.getJda().getSelfUser().getName()));
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> deferReply(String content) {
        return reply(content);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> deferReply(PresetBuilder preset) {
        return reply(preset);
    }
    
    @Override
    public CompletableFuture<FollowupReplyHandler> deferReply(EmbedBuilder embed) {
        return reply(embed);
    }
}
