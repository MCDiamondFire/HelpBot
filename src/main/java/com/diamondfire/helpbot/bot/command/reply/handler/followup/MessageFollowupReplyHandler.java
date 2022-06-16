package com.diamondfire.helpbot.bot.command.reply.handler.followup;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.MessageReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MessageFollowupReplyHandler extends MessageReplyHandler implements FollowupReplyHandler {

    private final Message message;
    
    public MessageFollowupReplyHandler(Message message) {
        super(message.getTextChannel());
        this.message = message;
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginal(String content) {
        return message.editMessage(content).submit().thenAccept(message -> {});
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginal(PresetBuilder preset) {
        return editOriginal(preset.getEmbed());
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginal(EmbedBuilder builder) {
        return message.editMessageEmbeds(builder.build()).submit().thenAccept(message -> {});
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginalFile(String content, byte @NotNull [] file, @NotNull String name, @NotNull AttachmentOption @NotNull ... options) {
        return message.editMessage(content).addFile(file, name, options).submit().thenAccept(message -> {});
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginalFile(PresetBuilder preset, byte @NotNull [] file, @NotNull String name, @NotNull AttachmentOption @NotNull ... options) {
        return editOriginalFile(preset.getEmbed(), file, name, options);
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginalFile(EmbedBuilder embed, byte @NotNull [] file, @NotNull String name, @NotNull AttachmentOption @NotNull ... options) {
        return message.editMessageEmbeds(embed.build()).addFile(file, name, options).submit().thenAccept(message -> {});
    }
}
