package com.diamondfire.helpbot.bot.command.reply.handler.followup;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.InteractionReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class InteractionFollowupReplyHandler extends InteractionReplyHandler implements FollowupReplyHandler {
    
    private final InteractionHook interactionHook;
    
    public InteractionFollowupReplyHandler(InteractionHook interactionHook) {
        super(interactionHook.getInteraction());
        this.interactionHook = interactionHook;
    }
    
    @Override
    public void editOriginal(String content) {
        interactionHook.editOriginal(content).queue();
    }
    
    @Override
    public void editOriginal(PresetBuilder preset) {
        editOriginal(preset.getEmbed());
    }
    
    @Override
    public void editOriginal(EmbedBuilder builder) {
        interactionHook.editOriginalEmbeds(builder.build()).queue();
    }
    
    @Override
    public void editOriginalFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        editOriginalFile(preset.getEmbed(), file, name, options);
    }
    
    @Override
    public void editOriginalFile(EmbedBuilder embed, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        interactionHook.editOriginalEmbeds(embed.build()).addFile(file, name, options).queue();
    }
    
    @Override
    public void editOriginalFile(String content, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        interactionHook.editOriginal(content).addFile(file, name, options).queue();
    }
}
