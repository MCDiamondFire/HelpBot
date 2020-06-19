package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.StringArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class SessionStatsCommand extends AbstractPlayerCommand {

    @Override
    public String getName() {
        return "sessionstats";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"sesstats"};
    }

    @Override
    public String getDescription() {
        return "Gets a certain users session stats.";
    }

    @Override
    public ValueArgument<String> getArgument() {
        return new StringArg("Username", false);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        EmbedBuilder builder = new EmbedBuilder();

        new SingleQueryBuilder()
                .query("SELECT COUNT(*) AS count," +
                        "SUM(duration) AS total_duration," +
                        "MIN(time) AS earliest_time," +
                        "MAX(time) AS latest_time," +
                        "COUNT(DISTINCT staff) AS unique_helped FROM support_sessions WHERE name = ?;", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((resultTable) -> {
                    if (resultTable.getInt("count") == 0) {
                        builder.clearFields();
                        builder.setTitle("Player has not been in a session!");
                        return;
                    }
                    builder.setAuthor(player, null, "https://mc-heads.net/head/" + player);
                    builder.setTitle("Session Stats");

                    new SingleQueryBuilder()
                            .query("SELECT COUNT(*) AS count FROM support_sessions " +
                                    "WHERE name = ? AND time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;", (statement) -> {
                                statement.setString(1, player);
                            })
                            .onQuery((resultBadTable) -> {
                                builder.addField("Sessions this month:", resultBadTable.getInt("count") + "", true);
                            }).execute();

                    builder.addField("Total Sessions", resultTable.getInt("count") + "", true);
                    builder.addField("Unique Support Members", resultTable.getInt("unique_helped") + "", true);
                    builder.addField("Total Session Time", StringUtil.formatMilliTime(resultTable.getLong("total_duration")), true);
                    builder.addField("Earliest Session", StringUtil.formatDate(resultTable.getDate("earliest_time")), true);
                    builder.addField("Latest Session", StringUtil.formatDate(resultTable.getDate("latest_time")), true);

                    new SingleQueryBuilder()
                            .query("SELECT AVG(duration) AS average_duration," +
                                    "MIN(duration) AS shortest_duration," +
                                    "MAX(duration) AS longest_duration " +
                                    "FROM support_sessions WHERE duration != 0 AND name = ?;", (statement) -> {
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
