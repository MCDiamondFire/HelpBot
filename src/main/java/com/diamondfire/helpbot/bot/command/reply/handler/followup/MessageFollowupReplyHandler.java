package com.diamondfire.helpbot.bot.command.reply.handler.followup;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.MessageReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MessageFollowupReplyHandler extends MessageReplyHandler implements FollowupReplyHandler {

    private final Message message;
    public MessageFollowupReplyHandler(Message message) {
        super(message.getTextChannel());
        this.message = message;
    }

    public void editOriginal(String content) {
        message.editMessage(content).queue();
    }

    public void editOriginal(PresetBuilder preset) {
        editOriginal(preset.getEmbed());
    }

    public void editOriginal(EmbedBuilder builder) {
        message.editMessageEmbeds(builder.build()).queue();
    }

    public void editOriginalFile(String content, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        message.editMessage(content).addFile(file, name, options).queue();
    }

    public void editOriginalFile(PresetBuilder preset, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        editOriginalFile(preset.getEmbed(), file, name, options);
    }

    public void editOriginalFile(EmbedBuilder embed, @NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        message.editMessageEmbeds(embed.build()).addFile(file, name, options).queue();
    }


}
