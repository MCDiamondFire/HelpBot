package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;

import java.sql.ResultSet;
import java.util.*;


public class StatsGraphCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "statsgraph";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"sessiongraph", "sgraph"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph of a support member's sessions.")
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
        new DatabaseQuery()
                .query(new BasicQuery("WITH RECURSIVE all_dates(dt) AS (\n" +
                        "    SELECT (SELECT DATE(time) FROM support_sessions WHERE staff = ? ORDER BY time LIMIT 1) dt\n" +
                        "        UNION ALL\n" +
                        "    SELECT dt + INTERVAl 1 DAY FROM all_dates WHERE dt + INTERVAL 1 DAY <= CURRENT_TIMESTAMP()\n" +
                        ")\n" +
                        "SELECT dates.dt date, COALESCE(t.total, 0) AS total FROM all_dates dates LEFT JOIN (SELECT DATE(time) AS date, COUNT(time) AS total FROM support_sessions WHERE staff = ? GROUP BY DATE(time)) t ON t.date = dates.dt ORDER BY dates.dt",
                        (statement) -> {
                            statement.setString(1, player);
                            statement.setString(2, player);
                        }))
                .compile()
                .run((query) -> {
                    Map<GraphableEntry<?>, Integer> dates = new LinkedHashMap<>();
                    for (ResultSet set : query) {
                        dates.put(new DateEntry(set.getDate("date")), set.getInt("total"));
                    }
                    
                    event.getChannel().sendFile(new ChartGraphBuilder()
                            .setGraphName(String.format("%s's sessions", player))
                            .createGraph(dates)).queue();
                    
                });
        
    }
    
}
