package com.diamondfire.helpbot.bot.command.reply.feature;

import net.dv8tion.jda.api.EmbedBuilder;

public interface ReplyPreset {

    void applyFeature(EmbedBuilder builder);
}
