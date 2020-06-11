package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

public class StatsCommand extends AbstractPlayerCommand {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Get info on the a certain user's support stats.";
    }

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Username", false);
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }


    @Override
    protected void execute(CommandEvent event, String player) {
        EmbedBuilder builder = new EmbedBuilder();

        new SingleQueryBuilder()
                .query("SELECT COUNT(*) AS count, (COUNT(*) < 5) AS bad FROM support_sessions " +
                        "WHERE staff = ? AND time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((resultTable) -> {
                    builder.addField("Bad?", resultTable.getInt("bad") == 1 ? "Yes!" : "No", true);
                    builder.addField("Monthly Sessions", resultTable.getInt("count") + "", true);
                }).execute();

        new SingleQueryBuilder()
                .query("SELECT COUNT(*) AS count," +
                        "MIN(time) as earliest_time," +
                        "MAX(time) AS latest_time," +
                        "COUNT(DISTINCT name) AS unique_helped FROM support_sessions WHERE staff = ?;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((resultTable) -> {
                    if (resultTable.getInt("count") == 0) {
                        builder.clearFields();
                        builder.setTitle("Player has no stats!");
                        event.getChannel().sendMessage(builder.build()).queue();
                        return;
                    }

                    builder.addField("Total Sessions", resultTable.getInt("count") + "", true);
                    builder.addField("Unique Players", resultTable.getInt("unique_helped") + "", true);
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
                                builder.addField("Average Session Time", StringUtil.formatMilliTime(resultTableTime.getInt(("average_duration"))), true);
                                builder.addField("Shortest Session Time", StringUtil.formatMilliTime(resultTableTime.getInt("shortest_duration")), true);
                                builder.addField("Longest Session Time", StringUtil.formatMilliTime(resultTableTime.getInt("longest_duration")), true);
                            }).execute();

                }).execute();

        builder.setAuthor(player, null, "https://mc-heads.net/head/" + player);
        builder.setTitle("Support Stats");

        event.getChannel().sendMessage(builder.build()).queue();

    }



}
