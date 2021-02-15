package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.TimeOffsetArgument;
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

import java.sql.*;
import java.time.Instant;

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
                                .optional(),
                        new HelpContextArgument()
                                .name("timeframe")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return super.compileArguments()
                .addArgument("timeframe",
                        new SingleArgumentContainer<>(new TimeOffsetArgument(true)).optional(null));
    }
    
    @Override
    public Permission getPermission() {
        return Permission.RETIRED_SUPPORT;
    }
    
    @Override
    protected void execute(CommandEvent event, String player) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Support Stats", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(*) AS count," +
                        "SUM(duration) AS total_duration," +
                        "MIN(time) AS earliest_time," +
                        "MAX(time) AS latest_time," +
                        "COUNT(DISTINCT name) AS unique_helped, staff FROM support_sessions WHERE staff = ? AND (time > ? OR ?);",
                        (statement) -> {
                            statement.setString(1, player);
                            
                            java.util.Date date = event.getArgument("timeframe");
                            Timestamp sqlTimestamp = null;
                            if (date != null) {
                                sqlTimestamp = DateUtil.toTimeStamp(date);
                            }
                            
                            statement.setTimestamp(2, sqlTimestamp);
                            statement.setBoolean(3, date == null);
                        }))
                .compile()
                .run((result) -> {
                    ResultSet set = result.getResult();
                    if (set.getInt("count") == 0) {
                        embed.clear();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player does not have any stats!"));
                        return;
                    }
                    
                    String formattedName = set.getString("staff");
                    preset.withPreset(
                            new MinecraftUserPreset(formattedName)
                    );
                    
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT COUNT(*) AS count, SUM(duration) AS total_time, " +
                                    "? IN (SELECT players.name FROM hypercube.ranks, hypercube.players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0 AND ranks.administration = 0) AS support," +
                                    "(COUNT(*) < 5) AS bad FROM support_sessions WHERE staff = ? AND time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;", (statement) -> {
                                statement.setString(1, player);
                                statement.setString(2, player);
                            }))
                            .compile()
                            .run((resultBadTable) -> {
                                ResultSet setBad = resultBadTable.getResult();
                                embed.addField("Monthly Sessions", FormatUtil.formatNumber(setBad.getInt("count")), true);
                                embed.addField("Monthly Time", FormatUtil.formatMilliTime(setBad.getLong("total_time")), true);
                                if (setBad.getBoolean("support")) {
                                    new DatabaseQuery()
                                            .query(new BasicQuery("SELECT (time + INTERVAL 30 DAY) AS bad_time " +
                                                    "FROM support_sessions WHERE staff = ?" +
                                                    "ORDER BY TIME DESC LIMIT 1 OFFSET 4;", (statement) -> statement.setString(1, player)))
                                            .compile()
                                            .run((resultBad) -> {
                                                if (resultBad.isEmpty()) {
                                                    return;
                                                }
                                                Timestamp date = resultBad.getResult().getTimestamp("bad_time");
                                                
                                                if (date.toInstant().isBefore(Instant.now())) {
                                                    embed.addField("Is in support bad", "Entered bad on " + FormatUtil.formatDate(date), true);
                                                } else {
                                                    embed.addField("Isn't in support bad", "Enters bad on " + FormatUtil.formatDate(date), true);
                                                }
                                            });
                                }
                            });
                    
                    embed.addField("Total Sessions", FormatUtil.formatNumber(set.getInt("count")), true);
                    embed.addField("Unique Players", FormatUtil.formatNumber(set.getInt("unique_helped")), true);
                    embed.addField("Total Session Time", FormatUtil.formatMilliTime(set.getLong("total_duration")), true);
                    embed.addField("Earliest Session", FormatUtil.formatDate(set.getDate("earliest_time")), true);
                    embed.addField("Latest Session", FormatUtil.formatDate(set.getDate("latest_time")), true);
                    
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT AVG(duration) AS average_duration," +
                                    "MIN(duration) AS shortest_duration," +
                                    "MAX(duration) AS longest_duration " +
                                    "FROM support_sessions WHERE duration != 0 AND staff = ?;", (statement) -> statement.setString(1, player)))
                            .compile()
                            .run((resultTableTime) -> {
                                ResultSet timeSet = resultTableTime.getResult();
                                embed.addField("Average Session Time", FormatUtil.formatMilliTime(timeSet.getLong("average_duration")), true);
                                embed.addField("Shortest Session Time", FormatUtil.formatMilliTime(timeSet.getLong("shortest_duration")), true);
                                embed.addField("Longest Session Time", FormatUtil.formatMilliTime(timeSet.getLong("longest_duration")), true);
                            });
                    
                });
        
        event.reply(preset);
    }
    
}