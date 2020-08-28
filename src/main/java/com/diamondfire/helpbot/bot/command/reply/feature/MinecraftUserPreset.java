package com.diamondfire.helpbot.bot.command.reply.feature;

import net.dv8tion.jda.api.EmbedBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.lang.System;

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
        builder.setAuthor(player, null, "https://external-content.duckduckgo.com/iu/?reload=" + System.currentTimeMillis() + "&u=" + "https://mc-heads.net/head/" + URLEncoder.encode((uuid == null ? player : uuid), StandardCharsets.UTF_8));
    }
}
