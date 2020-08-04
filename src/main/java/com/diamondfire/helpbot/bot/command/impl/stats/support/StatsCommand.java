package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Date;
import java.time.LocalDate;

public class StatsCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets a certain support member's session stats.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Support Stats", null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT COUNT(*) AS count," +
                        "SUM(duration) AS total_duration," +
                        "MIN(time) AS earliest_time," +
                        "MAX(time) AS latest_time," +
                        "COUNT(DISTINCT name) AS unique_helped, staff FROM support_sessions WHERE staff = ?;", (statement) -> statement.setString(1, player))
                .onQuery((resultTable) -> {
                    if (resultTable.getInt("count") == 0) {
                        embed.clear();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player does not have any stats!"));
                        return;
                    }

                    String formattedName = resultTable.getString("staff");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName)
                    );

                    new SingleQueryBuilder()
                            .query("SELECT COUNT(*) AS count," +
                                    "? IN (SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0 AND ranks.developer IS NULL) AS support," +
                                    "(COUNT(*) < 5) AS bad FROM support_sessions WHERE staff = ? AND time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;", (statement) -> {
                                statement.setString(1, player);
                                statement.setString(2, player);
                            })
                            .onQuery((resultBadTable) -> {
                                embed.addField("Monthly Sessions", StringUtil.formatNumber(resultBadTable.getInt("count")), true);
                                if (resultBadTable.getBoolean("support")) {
                                    embed.addField("Bad?", resultBadTable.getInt("bad") == 1 ? "Yes!" : "No", true);
                                    new SingleQueryBuilder()
                                            .query(" SELECT (time + INTERVAL 30 DAY) AS bad_time " +
                                                    "FROM support_sessions WHERE staff = ?" +
                                                    "ORDER BY TIME DESC LIMIT 1 OFFSET 4;", (statement) -> statement.setString(1, player))
                                            .onQuery((resultBadTableTime) -> {
                                                Date date = resultBadTableTime.getDate("bad_time");

                                                if (date.toLocalDate().isBefore(LocalDate.now())) {
                                                    embed.addField("Entered Bad", StringUtil.formatDate(date), true);
                                                } else {
                                                    embed.addField("Enters Bad", StringUtil.formatDate(date), true);
                                                }
                                            }).execute();
                                }
                            }).execute();


                    embed.addField("Total Sessions", StringUtil.formatNumber(resultTable.getInt("count")), true);
                    embed.addField("Unique Players", StringUtil.formatNumber(resultTable.getInt("unique_helped")), true);
                    embed.addField("Total Session Time", StringUtil.formatMilliTime(resultTable.getLong("total_duration")), true);
                    embed.addField("Earliest Session", StringUtil.formatDate(resultTable.getDate("earliest_time")), true);
                    embed.addField("Latest Session", StringUtil.formatDate(resultTable.getDate("latest_time")), true);

                    new SingleQueryBuilder()
                            .query("SELECT AVG(duration) AS average_duration," +
                                    "MIN(duration) AS shortest_duration," +
                                    "MAX(duration) AS longest_duration " +
                                    "FROM support_sessions WHERE duration != 0 AND staff = ?;", (statement) -> statement.setString(1, player))
                            .onQuery((resultTableTime) -> {
                                embed.addField("Average Session Time", StringUtil.formatMilliTime(resultTableTime.getLong("average_duration")), true);
                                embed.addField("Shortest Session Time", StringUtil.formatMilliTime(resultTableTime.getLong("shortest_duration")), true);
                                embed.addField("Longest Session Time", StringUtil.formatMilliTime(resultTableTime.getLong("longest_duration")), true);
                            }).execute();

                }).execute();

        event.reply(preset);

    }

}
