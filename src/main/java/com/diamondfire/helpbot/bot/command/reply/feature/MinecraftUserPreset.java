package com.diamondfire.helpbot.bot.command.reply.feature;

import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.minecraft.Player;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

public class MinecraftUserPreset implements ReplyPreset {
    
    private final String uuid;
    private final String player;
    
    public MinecraftUserPreset(Player player) {
        this.player = player.name();
        this.uuid = player.uuidString();
    }
    
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
        builder.setAuthor(player, null, Util.getPlayerHead(uuid == null ? player : uuid));
    }
}
