package com.diamondfire.helpbot.bot.command.reply.handler.followup;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.InteractionReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class InteractionFollowupReplyHandler extends InteractionReplyHandler implements FollowupReplyHandler {
    
    private final InteractionHook interactionHook;
    
    public InteractionFollowupReplyHandler(InteractionHook interactionHook) {
        super(interactionHook.getInteraction());
        this.interactionHook = interactionHook;
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginal(String content) {
        return interactionHook.editOriginal(content).submit().thenAccept(message -> {});
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginal(PresetBuilder preset) {
        return editOriginal(preset.getEmbed());
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginal(EmbedBuilder builder) {
        return interactionHook.editOriginalEmbeds(builder.build()).submit().thenAccept(message -> {});
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginalFile(PresetBuilder preset, byte @NotNull [] file, @NotNull String name, @NotNull AttachmentOption @NotNull ... options) {
        return editOriginalFile(preset.getEmbed(), file, name, options);
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginalFile(EmbedBuilder embed, byte @NotNull [] file, @NotNull String name, @NotNull AttachmentOption @NotNull ... options) {
        return interactionHook.editOriginalEmbeds(embed.build()).addFile(file, name, options).submit().thenAccept(message -> {});
    }
    
    @Override
    public @NotNull CompletableFuture<Void> editOriginalFile(String content, byte @NotNull [] file, @NotNull String name, @NotNull AttachmentOption @NotNull ... options) {
        return interactionHook.editOriginal(content).addFile(file, name, options).submit().thenAccept(message -> {});
    }
}
