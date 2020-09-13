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
import com.diamondfire.helpbot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.util.*;


public class WhoHelpedCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "whohelped";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets all staff members that helped a certain player. Name changes are not counted, so use ?names to find previous names.")
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
                        new MinecraftUserPreset(player),
                        new InformativeReply(InformativeReplyType.INFO, "Players who have helped " + player, null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(staff) AS total, staff,name FROM support_sessions WHERE name = ? GROUP BY staff ORDER BY count(staff) DESC;", (statement) -> statement.setString(1, player)))
                .compile()
                .run((result) -> {

                    if (result.isEmpty()) {
                        embed.setDescription("Nobody!");
                        return;
                    }

                    ResultSet set = result.getResult();
                    List<String> sessions = new ArrayList<>();
                    String formattedName = set.getString("name");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName),
                            new InformativeReply(InformativeReplyType.INFO, "Players who have helped " + formattedName, null)
                    );

                    for (ResultSet ignored : result) {
                        sessions.add(set.getInt("total") + " " + set.getString("staff"));
                    }

                    EmbedUtils.addFields(embed, sessions, true);

                });
        event.reply(preset);
    }

}
