package com.diamondfire.helpbot.bot.command.reply.feature.informative;

import com.diamondfire.helpbot.bot.command.reply.feature.ReplyPreset;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class InformativeReply implements ReplyPreset {
    
    final InformativeReplyType type;
    final String title;
    final String description;
    final Color color;
    
    public InformativeReply(InformativeReplyType type, String description) {
        this.type = type;
        this.color = type.getColor();
        this.description = description;
        this.title = type.getTitle();
    }
    
    public InformativeReply(InformativeReplyType type, String title, @Nullable String description) {
        this.type = type;
        this.color = type.getColor();
        this.description = description;
        this.title = title;
    }
    
    
    public InformativeReplyType getType() {
        return type;
    }
    
    @Override
    public void applyFeature(EmbedBuilder builder) {
        builder.setTitle(title);
        if (description != null) {
            builder.setDescription(description);
        }
        builder.setColor(color);
    }
}
