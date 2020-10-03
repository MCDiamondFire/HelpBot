package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
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
        PresetBuilder preset = new PresetBuilder();
        EmbedBuilder embed = preset.getEmbed();

        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(name) AS total, name,staff FROM support_sessions WHERE staff = ? GROUP BY name ORDER BY count(name) DESC LIMIT 25;", (statement) -> statement.setString(1, player)))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        preset.withPreset(new MinecraftUserPreset(player));
                        embed.setDescription("Nobody!");
                        return;
                    }

                    ResultSet set = result.getResult();
                    List<String> sessions = new ArrayList<>();
                    String formattedName = set.getString("staff");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName),
                            new InformativeReply(InformativeReplyType.INFO, String.format("Players %s has Helped", formattedName), null)
                    );

                    for (ResultSet setUser : result) {
                        sessions.add(FormatUtil.formatNumber(setUser.getInt("total")) + " " + setUser.getString("name"));
                    }

                    EmbedUtil.addFields(embed, sessions, true);

                });

        event.reply(preset);
    }

}
