package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
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
        EmbedBuilder builder = new EmbedBuilder();

        new SingleQueryBuilder()
                .query("SELECT COUNT(*) AS count," +
                        "SUM(duration) AS total_duration," +
                        "MIN(time) AS earliest_time," +
                        "MAX(time) AS latest_time," +
                        "COUNT(DISTINCT name) AS unique_helped FROM support_sessions WHERE staff = ?;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((resultTable) -> {
                    if (resultTable.getInt("count") == 0) {
                        builder.clearFields();
                        builder.setTitle("Player has no stats!");
                        return;
                    }
                    builder.setAuthor(player, null, "https://mc-heads.net/head/" + player);
                    builder.setTitle("Support Stats");

                    new SingleQueryBuilder()
                            .query("SELECT COUNT(*) AS count," +
                                    "? IN (SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0 AND ranks.developer IS NULL) AS support," +
                                    "(COUNT(*) < 5) AS bad FROM support_sessions WHERE staff = ? AND time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;", (statement) -> {
                                statement.setString(1, player);
                                statement.setString(2, player);
                            })
                            .onQuery((resultBadTable) -> {
                                builder.addField("Monthly Sessions", resultBadTable.getInt("count") + "", true);
                                if (resultBadTable.getBoolean("support")) {
                                    builder.addField("Bad?", resultBadTable.getInt("bad") == 1 ? "Yes!" : "No", true);
                                    new SingleQueryBuilder()
                                            .query(" SELECT (time + INTERVAL 30 DAY) AS bad_time " +
                                                    "FROM support_sessions WHERE staff = ?" +
                                                    "ORDER BY TIME DESC LIMIT 1 OFFSET 4;", (statement) -> {
                                                statement.setString(1, player);
                                            })
                                            .onQuery((resultBadTableTime) -> {
                                                Date date = resultBadTableTime.getDate("bad_time");

                                                if (date.toLocalDate().isBefore(LocalDate.now())) {
                                                    builder.addField("Entered Bad", StringUtil.formatDate(date), true);
                                                } else {
                                                    builder.addField("Enters Bad", StringUtil.formatDate(date), true);
                                                }
                                            }).execute();
                                }
                            }).execute();


                    builder.addField("Total Sessions", resultTable.getInt("count") + "", true);
                    builder.addField("Unique Players", resultTable.getInt("unique_helped") + "", true);
                    builder.addField("Total Session Time", StringUtil.formatMilliTime(resultTable.getLong("total_duration")), true);
                    builder.addField("Earliest Session", StringUtil.formatDate(resultTable.getDate("earliest_time")), true);
                    builder.addField("Latest Session", StringUtil.formatDate(resultTable.getDate("latest_time")), true);

                    new SingleQueryBuilder()
                            .query("SELECT AVG(duration) AS average_duration," +
                                    "MIN(duration) AS shortest_duration," +
                                    "MAX(duration) AS longest_duration " +
                                    "FROM support_sessions WHERE duration != 0 AND staff = ?;", (statement) -> {
                                statement.setString(1, player);
                            })
                            .onQuery((resultTableTime) -> {
                                builder.addField("Average Session Time", StringUtil.formatMilliTime(resultTableTime.getLong("average_duration")), true);
                                builder.addField("Shortest Session Time", StringUtil.formatMilliTime(resultTableTime.getLong("shortest_duration")), true);
                                builder.addField("Longest Session Time", StringUtil.formatMilliTime(resultTableTime.getLong("longest_duration")), true);
                            }).execute();

                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();

    }


}
