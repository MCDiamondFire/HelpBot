package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.followup.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class SlashReplyHandler implements ReplyHandler {
    private final CommandInteraction internalEvent;
    public SlashReplyHandler(CommandInteraction internalEvent) {
        this.internalEvent = internalEvent;
    }
    
    public CompletableFuture<FollowupReplyHandler> reply(String content) {
        return internalEvent.reply(content)
                .submit()
                .thenApply(SlashFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> reply(PresetBuilder preset) {
        return reply(preset.getEmbed());
    }
    
    public CompletableFuture<FollowupReplyHandler> reply(EmbedBuilder builder) {
        return internalEvent.replyEmbeds(builder.build())
                .submit()
                .thenApply(SlashFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> replyFile(String content, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return internalEvent.reply(content)
                .addFile(file, name, options)
                .submit()
                .thenApply(SlashFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> replyFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return replyFile(preset.getEmbed(), file, name, options);
    }
    
    public CompletableFuture<FollowupReplyHandler> replyFile(EmbedBuilder embed, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return internalEvent.replyEmbeds(embed.build())
                .addFile(file, name, options)
                .submit()
                .thenApply(SlashFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply() {
        return internalEvent.deferReply()
                .submit()
                .thenApply(SlashFollowupReplyHandler::new);
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply(String content) {
        return deferReply();
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply(PresetBuilder preset) {
        return deferReply();
    }
    
    public CompletableFuture<FollowupReplyHandler> deferReply(EmbedBuilder embed) {
        return deferReply();
    }
}
