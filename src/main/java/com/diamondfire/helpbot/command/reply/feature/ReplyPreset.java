package com.diamondfire.helpbot.command.reply.feature;

import net.dv8tion.jda.api.EmbedBuilder;

public interface ReplyPreset {

    void applyFeature(EmbedBuilder builder);
}
