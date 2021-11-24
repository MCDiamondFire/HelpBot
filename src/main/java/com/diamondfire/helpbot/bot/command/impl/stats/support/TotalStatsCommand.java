package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;

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
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }
    
    // It would be nice if someone with more SQL experience could remake these to be more condensed.
    @Override
    public void run(CommandEvent event) {
        
        event.getReplyHandler()
                .deferReply(new PresetBuilder().withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Please wait a moment, statistics are currently being calculated.")))
                .thenAccept(followupReplyHandler -> followupReplyHandler.editOriginal(createStatsEmbed()));
    }
    
    private PresetBuilder createStatsEmbed() {
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
                        "COUNT(DISTINCT name) AS unique_helped FROM support_sessions;"))
                .compile()
                .run((resultTable) -> {
                    ResultSet resultset = resultTable.getResult();
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT COUNT(*) AS count FROM support_sessions WHERE time > CURRENT_TIMESTAMP() - INTERVAL 30 DAY;"))
                            .compile()
                            .run((resultBadTable) -> embed.addField("Monthly Sessions", FormatUtil.formatNumber(resultBadTable.getResult().getInt("count")), false));
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT DISTINCT staff, " +
                                    "COUNT(*) as total FROM support_sessions " +
                                    "GROUP BY staff ORDER BY total DESC LIMIT 1;"))
                            .compile()
                            .run((set) -> {
                                ResultSet rs = set.getResult();
                                embed.addField("Total Sessions", FormatUtil.formatNumber(resultset.getInt("count")) +
                                        String.format("\nHighest: %s (%s)", rs.getString("staff"), FormatUtil.formatNumber(rs.getInt("total"))), false);
                            });
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT staff, COUNT(name) AS total FROM " +
                                    "(SELECT UNIQUE name, staff FROM support_sessions WHERE duration != 0) a " +
                                    "GROUP BY staff ORDER BY total DESC LIMIT 1;"))
                            .compile()
                            .run((set) -> {
                                ResultSet rs = set.getResult();
                                embed.addField("Unique Players", FormatUtil.formatNumber(resultset.getInt("unique_helped")) +
                                        String.format("\nHighest: %s (%s)", rs.getString("staff"), FormatUtil.formatNumber(rs.getInt("total"))), false);
                            });
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT DISTINCT staff, SUM(duration) as total FROM support_sessions GROUP BY staff ORDER BY total DESC LIMIT 1;"))
                            .compile()
                            .run((set) -> {
                                ResultSet rs = set.getResult();
                                embed.addField("Total Session Time", FormatUtil.formatMilliTime(resultset.getLong("total_duration")) +
                                        String.format("\nHighest: %s (%s)", rs.getString("staff"), FormatUtil.formatMilliTime(rs.getLong("total"))), false);
                            });
                
                    embed.addField("Earliest Session", FormatUtil.formatDate(resultset.getDate("earliest_time")), false);
                    embed.addField("Latest Session", FormatUtil.formatDate(resultset.getDate("latest_time")), false);
                
                
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT staff, AVG(duration) AS total FROM " +
                                    "(SELECT staff, duration FROM support_sessions WHERE duration != 0) a " +
                                    "GROUP BY staff ORDER BY total DESC LIMIT 1;"))
                            .compile()
                            .run((set) -> {
                                ResultSet resultSet = set.getResult();
                                new DatabaseQuery()
                                        .query(new BasicQuery("SELECT AVG(duration) AS average_duration FROM support_sessions WHERE duration != 0"))
                                        .compile()
                                        .run((resultTableTime) -> {
                                            ResultSet rs = resultTableTime.getResult();
                                            embed.addField("Average Session Time", FormatUtil.formatMilliTime(rs.getLong("average_duration")) +
                                                    String.format("\nHighest Average: %s (%s)", resultSet.getString("staff"), FormatUtil.formatMilliTime(resultSet.getLong("total"))), false);
                                        });
                            });
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT DISTINCT staff, MIN(duration) as total FROM support_sessions WHERE duration != 0 ORDER BY total DESC LIMIT 1;"))
                            .compile()
                            .run((set) -> {
                                ResultSet rs = set.getResult();
                                embed.addField("Shortest Session Time", FormatUtil.formatMilliTime(rs.getLong("total")) +
                                        String.format("\nHeld By: %s", rs.getString("staff")), false);
                            });
                    new DatabaseQuery()
                            .query(new BasicQuery("SELECT DISTINCT staff, MAX(duration) AS total FROM support_sessions GROUP BY staff ORDER BY total DESC LIMIT 1;"))
                            .compile()
                            .run((set) -> {
                                ResultSet rs = set.getResult();
                                embed.addField("Longest Session Time", FormatUtil.formatMilliTime(rs.getLong("total")) +
                                        String.format("\nHeld By: %s", rs.getString("staff")), false);
                            });
                
                });
        
        return preset;
    }
    
}
