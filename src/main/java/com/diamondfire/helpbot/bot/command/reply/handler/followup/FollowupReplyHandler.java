package com.diamondfire.helpbot.bot.command.reply.handler.followup;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.ReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface FollowupReplyHandler extends ReplyHandler {
    @NotNull CompletableFuture<Void> editOriginal(String content);
    
    @NotNull CompletableFuture<Void> editOriginal(PresetBuilder preset);
    
    @NotNull CompletableFuture<Void> editOriginal(EmbedBuilder builder);
    
    @NotNull CompletableFuture<Void> editOriginalFile(PresetBuilder preset, @Nonnull final byte[] file, @Nonnull final String name, @Nonnull AttachmentOption... options);
    
    @NotNull CompletableFuture<Void> editOriginalFile(EmbedBuilder embed, @Nonnull final byte[] file, @Nonnull final String name, @Nonnull AttachmentOption... options);
    
    @NotNull CompletableFuture<Void> editOriginalFile(String content, @Nonnull final byte[] file, @Nonnull final String name, @Nonnull AttachmentOption... options);
}
