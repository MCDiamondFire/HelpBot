package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.followup.FollowupReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ReplyHandler {
    // Standard Replies
    
    @NotNull CompletableFuture<FollowupReplyHandler> reply(String content);
    
    @NotNull CompletableFuture<FollowupReplyHandler> reply(PresetBuilder preset);
    
    @NotNull CompletableFuture<FollowupReplyHandler> reply(EmbedBuilder builder);
    
    // File Replies
    
    @NotNull CompletableFuture<FollowupReplyHandler> replyFile(String content, final byte @NotNull [] data, @NotNull final String name, @NotNull AttachmentOption... options);
    
    @NotNull CompletableFuture<FollowupReplyHandler> replyFile(PresetBuilder preset, final byte @NotNull [] data, @NotNull final String name, @NotNull AttachmentOption... options);
    
    @NotNull CompletableFuture<FollowupReplyHandler> replyFile(EmbedBuilder embed, final byte @NotNull [] data, @NotNull final String name, @NotNull AttachmentOption... options);
    
    // Deferred Replies
    
    @NotNull CompletableFuture<FollowupReplyHandler> deferReply();
    
    @NotNull CompletableFuture<FollowupReplyHandler> deferReply(String content);
    
    @NotNull CompletableFuture<FollowupReplyHandler> deferReply(PresetBuilder preset);
    
    @NotNull CompletableFuture<FollowupReplyHandler> deferReply(EmbedBuilder embed);
}
