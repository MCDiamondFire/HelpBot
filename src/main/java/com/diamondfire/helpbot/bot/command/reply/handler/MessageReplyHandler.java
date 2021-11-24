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
    
    public CompletableFuture<FollowupReplyHandler> reply(String content) {
        return channel.sendMessage(content)
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> reply(PresetBuilder preset) {
        return reply(preset.getEmbed());
    }
    
    public CompletableFuture<FollowupReplyHandler> reply(EmbedBuilder builder) {
        return channel.sendMessageEmbeds(builder.build())
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> replyFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return replyFile(preset.getEmbed(), file, name, options);
    }
    
    public CompletableFuture<FollowupReplyHandler> replyFile(EmbedBuilder embed, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return channel.sendMessageEmbeds(embed.build()).addFile(file, name, options)
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> replyFile(String content, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return channel.sendMessage(content).addFile(file, name, options)
                .submit()
                .thenApply(MessageFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply() {
        return reply(String.format("*%s is thinking...*", HelpBotInstance.getJda().getSelfUser().getName()));
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply(String content) {
        return reply(content);
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply(PresetBuilder preset) {
        return reply(preset);
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply(EmbedBuilder embed) {
        return reply(embed);
    }

}
