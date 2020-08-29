package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class TotalStatsCommand extends Command {

    @Override
    public String getName() {
        return "allstats";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets all support member's session stats.")
                .category(CommandCategory.SUPPORT);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    // It would be nice if someone with more SQL experience could remake these to be more condensed.
    @Override
    public void run(CommandEvent event) {
        PresetBuilder presetBefore = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Please wait a moment, statistics are currently being calculated.")
                );

        event.getReplyHandler().replyA(presetBefore).queue((msg) -> {
            PresetBuilder preset = new PresetBuilder()
                    .withPreset(
                            new InformativeReply(InformativeReplyType.INFO, "Support Stats", null)
                    );
            EmbedBuilder embed = preset.getEmbed();

            new SingleQueryBuilder()
                    .query("SELECT COUNT(*) AS count," +
                            "SUM(duration) AS total_duration," +
                            "MIN(time) AS earliest_time," +
                            "MAX(time) AS latest_time," +
                            "COUNT(DISTINCT name) AS unique_helped FROM support_sessions;")
                    .onQuery((resultTable) -> {
                        new SingleQueryBuilder()
                                .query("SELECT COUNT(*) AS count FROM support_sessions WHERE time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;")
                                .onQuery((resultBadTable) -> embed.addField("Monthly Sessions", FormatUtil.formatNumber(resultBadTable.getInt("count")), false)).execute();

                        new SingleQueryBuilder()
                                .query("SELECT DISTINCT staff, " +
                                        "COUNT(*) as total FROM support_sessions " +
                                        "GROUP BY staff ORDER BY total DESC LIMIT 1;")
                                .onQuery((set) -> embed.addField("Total Sessions", FormatUtil.formatNumber(resultTable.getInt("count")) +
                                        String.format("\nHighest: %s (%s)", set.getString("staff"), FormatUtil.formatNumber(set.getInt("total"))), false)).execute();

                        new SingleQueryBuilder()
                                .query("SELECT staff, COUNT(name) AS total FROM " +
                                        "(SELECT UNIQUE name, staff FROM support_sessions WHERE duration != 0) a " +
                                        "GROUP BY staff ORDER BY total DESC LIMIT 1;")
                                .onQuery((set) -> embed.addField("Unique Players", FormatUtil.formatNumber(resultTable.getInt("unique_helped")) +
                                        String.format("\nHighest: %s (%s)", set.getString("staff"), FormatUtil.formatNumber(set.getInt("total"))), false)).execute();


                        new SingleQueryBuilder()
                                .query("SELECT DISTINCT staff, SUM(duration) as total FROM support_sessions GROUP BY staff ORDER BY total DESC LIMIT 1;")
                                .onQuery((set) -> embed.addField("Total Session Time", FormatUtil.formatMilliTime(resultTable.getLong("total_duration")) +
                                        String.format("\nHighest: %s (%s)", set.getString("staff"), FormatUtil.formatMilliTime(set.getLong("total"))), false)).execute();

                        embed.addField("Earliest Session", FormatUtil.formatDate(resultTable.getDate("earliest_time")), false);
                        embed.addField("Latest Session", FormatUtil.formatDate(resultTable.getDate("latest_time")), false);


                        new SingleQueryBuilder()
                                .query("SELECT staff, AVG(duration) AS total FROM " +
                                        "(SELECT staff, duration FROM support_sessions WHERE duration != 0) a " +
                                        "GROUP BY staff ORDER BY total DESC LIMIT 1;")
                                .onQuery((set) -> new SingleQueryBuilder()
                                        .query("SELECT AVG(duration) AS average_duration FROM support_sessions WHERE duration != 0")
                                        .onQuery((resultTableTime) -> embed.addField("Average Session Time", FormatUtil.formatMilliTime(resultTableTime.getLong("average_duration")) +
                                                String.format("\nHighest Average: %s (%s)", set.getString("staff"), FormatUtil.formatMilliTime(set.getLong("total"))), false)).execute()).execute();
                        new SingleQueryBuilder()
                                .query("SELECT DISTINCT staff, MIN(duration) as total FROM support_sessions WHERE duration != 0 ORDER BY total DESC LIMIT 1;")
                                .onQuery((set) -> embed.addField("Shortest Session Time", FormatUtil.formatMilliTime(set.getLong("total")) +
                                        String.format("\nHeld By: %s", set.getString("staff")), false)).execute();
                        new SingleQueryBuilder()
                                .query("SELECT DISTINCT staff, MAX(duration) AS total FROM support_sessions GROUP BY staff ORDER BY total DESC LIMIT 1;")
                                .onQuery((set) -> embed.addField("Longest Session Time", FormatUtil.formatMilliTime(set.getLong("total")) +
                                        String.format("\nHeld By: %s", set.getString("staff")), false)).execute();

                    }).execute();

            msg.editMessage(embed.build()).queue();

        });

    }

}
