package com.diamondfire.helpbot.bot.command.reply;

import com.diamondfire.helpbot.bot.command.reply.feature.ReplyPreset;
import net.dv8tion.jda.api.EmbedBuilder;

public class PresetBuilder {

    private final EmbedBuilder builder = new EmbedBuilder();

    public PresetBuilder withPreset(ReplyPreset... presets) {
        for (ReplyPreset preset : presets) {
            preset.applyFeature(builder);
        }
        return this;
    }

    public EmbedBuilder getEmbed() {
        return builder;
    }
}
