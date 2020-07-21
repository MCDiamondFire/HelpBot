package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;


public class HelpedByCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "helpedby";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the first 25 players that were helped by a certain staff member.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("player")
                                .optional()
                );
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("Players %s has Helped", StringUtil.display(player)), null),
                        new MinecraftUserPreset(player)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT COUNT(name) AS total, name FROM support_sessions WHERE staff = ? GROUP BY name ORDER BY count(name) DESC LIMIT 25;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((query) -> {
                    List<String> sessions = new ArrayList<>();

                    do {
                        sessions.add(query.getInt("total") + " " + query.getString("name"));
                    } while (query.next());

                    Util.addFields(embed, sessions, true);

                })
                .onNotFound(() -> {
                    embed.setDescription("Nobody!");
                }).execute();

        event.reply(preset);
    }

}
