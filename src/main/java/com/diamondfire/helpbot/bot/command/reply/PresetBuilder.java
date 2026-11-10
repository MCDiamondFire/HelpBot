package com.diamondfire.helpbot.bot.command.reply;

import com.diamondfire.helpbot.bot.command.reply.feature.ReplyPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import net.dv8tion.jda.api.EmbedBuilder;

public class PresetBuilder {
    
    private final EmbedBuilder builder = new EmbedBuilder();
    private boolean error = false;
    
    public PresetBuilder withPreset(ReplyPreset... presets) {
        for (ReplyPreset preset : presets) {
            preset.applyFeature(builder);
            if (preset instanceof InformativeReply reply && reply.getType() == InformativeReplyType.ERROR) {
                error = true;
            }
        }
        return this;
    }
    
    public boolean isError() {
        return error;
    }
    
    public EmbedBuilder getEmbed() {
        return builder;
    }
}
