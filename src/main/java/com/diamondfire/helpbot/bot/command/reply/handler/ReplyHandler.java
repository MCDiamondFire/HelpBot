package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.followup.FollowupReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface ReplyHandler {
    // Standard Replies
    
    CompletableFuture<FollowupReplyHandler> reply(String content);
    
    CompletableFuture<FollowupReplyHandler> reply(PresetBuilder preset);
    
    CompletableFuture<FollowupReplyHandler> reply(EmbedBuilder builder);
    
    // File Replies
    
    CompletableFuture<FollowupReplyHandler> replyFile(String content, final byte[] data, @NotNull final String name, @NotNull AttachmentOption... options);
    
    CompletableFuture<FollowupReplyHandler> replyFile(PresetBuilder preset, final byte[] data, @NotNull final String name, @NotNull AttachmentOption... options);
    
    CompletableFuture<FollowupReplyHandler> replyFile(EmbedBuilder embed, final byte[] data, @NotNull final String name, @NotNull AttachmentOption... options);
    
    // Deferred Replies
    
    CompletableFuture<FollowupReplyHandler> deferReply();
    
    CompletableFuture<FollowupReplyHandler> deferReply(String content);
    
    CompletableFuture<FollowupReplyHandler> deferReply(PresetBuilder preset);
    
    CompletableFuture<FollowupReplyHandler> deferReply(EmbedBuilder embed);
}
