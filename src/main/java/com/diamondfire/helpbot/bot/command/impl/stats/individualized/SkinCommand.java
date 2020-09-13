package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;


public class SkinCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "skin";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a user's player skin.")
                .category(CommandCategory.PLAYER_STATS)
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
        //Discord didn't want to preview the skin, why? I don't know..
        EmbedBuilder embed = preset.getEmbed();
        embed.setImage("https://external-content.duckduckgo.com/iu/?reload=" + System.currentTimeMillis() + "&u=" + "https://mc-heads.net/body/" + player + "/180");
        event.reply(preset);
    }

}


