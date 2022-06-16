package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.followup.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class InteractionReplyHandler implements ReplyHandler {
    
    private final Interaction internalEvent;
    
    public InteractionReplyHandler(Interaction internalEvent) {
        this.internalEvent = internalEvent;
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> reply(String content) {
        return internalEvent.reply(content)
                .submit()
                .thenApply(InteractionFollowupReplyHandler::new);
    }
    
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> reply(PresetBuilder preset) {
        return reply(preset.getEmbed());
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> reply(EmbedBuilder builder) {
        return internalEvent.replyEmbeds(builder.build())
                .submit()
                .thenApply(InteractionFollowupReplyHandler::new);
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> replyFile(String content, @NotNull byte[] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return internalEvent.reply(content)
                .addFile(data, name, options)
                .submit()
                .thenApply(InteractionFollowupReplyHandler::new);
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> replyFile(PresetBuilder preset, byte @NotNull [] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return replyFile(preset.getEmbed(), data, name, options);
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> replyFile(EmbedBuilder embed, byte @NotNull [] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return internalEvent.replyEmbeds(embed.build())
                .addFile(data, name, options)
                .submit()
                .thenApply(InteractionFollowupReplyHandler::new);
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> deferReply() {
        return internalEvent.deferReply()
                .submit()
                .thenApply(InteractionFollowupReplyHandler::new);
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> deferReply(String content) {
        return deferReply();
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> deferReply(PresetBuilder preset) {
        return deferReply();
    }
    
    @Override
    public @NotNull CompletableFuture<FollowupReplyHandler> deferReply(EmbedBuilder embed) {
        return deferReply();
    }
}
