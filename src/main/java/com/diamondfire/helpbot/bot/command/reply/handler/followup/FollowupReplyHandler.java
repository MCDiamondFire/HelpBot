package com.diamondfire.helpbot.bot.command.reply.handler.followup;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.handler.ReplyHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.annotation.Nonnull;
import java.io.File;

public interface FollowupReplyHandler extends ReplyHandler {
    void editOriginal(String content);
    
    void editOriginal(PresetBuilder preset);
    
    void editOriginal(EmbedBuilder builder);
    
    void editOriginalFile(PresetBuilder preset, @Nonnull final File file, @Nonnull final String name, @Nonnull AttachmentOption... options);
    
    void editOriginalFile(EmbedBuilder embed, @Nonnull final File file, @Nonnull final String name, @Nonnull AttachmentOption... options);
    
    void editOriginalFile(String content, @Nonnull final File file, @Nonnull final String name, @Nonnull AttachmentOption... options);
}
