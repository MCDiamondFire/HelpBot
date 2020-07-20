package com.diamondfire.helpbot.command.reply.feature;

import net.dv8tion.jda.api.EmbedBuilder;

public class MinecraftUserPreset implements ReplyPreset {

    private final String uuid;
    private final String player;

    public MinecraftUserPreset(String player) {
        this.player = player;
        this.uuid = null;
    }

    public MinecraftUserPreset(String player, String uuid) {
        this.player = player;
        this.uuid = uuid;
    }

    @Override
    public void applyFeature(EmbedBuilder builder) {
        builder.setAuthor(player, null, "https://mc-heads.net/head/" + (uuid == null ? player : uuid));
    }
}
