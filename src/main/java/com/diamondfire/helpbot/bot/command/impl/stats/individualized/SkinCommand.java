package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.*;
import java.net.*;
import java.util.*;


public class SkinCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "skin";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a user's player skin.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("%s's Skin", player), null),
                        new MinecraftUserPreset(player)
                );
        EmbedBuilder embed = preset.getEmbed();
        try {
           embed.setImage("https://mc-heads.net/body/" + player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        event.reply(preset);
    }

}


