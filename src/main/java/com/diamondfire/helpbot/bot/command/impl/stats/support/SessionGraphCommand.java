package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.util.DateUtil;

import java.sql.ResultSet;
import java.util.*;


public class SessionGraphCommand extends Command {
    
    @Override
    public String getName() {
        return "sessiongraph";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"sessiongraph"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph of all sessions done in a specific day.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("date")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("date",
                        new DateArgument());
        
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Date date = event.getArgument("date");
        java.sql.Date sqlDate = DateUtil.toSqlDate(date);
        
        new DatabaseQuery()
                .query(new BasicQuery("WITH RECURSIVE all_dates(dt) AS ( SELECT (SELECT DATE(time) FROM hypercube.support_sessions WHERE time > CURRENT_TIMESTAMP() - INTERVAL 24 HOURS ORDER BY time) dt UNION ALL SELECT dt + ? FROM all_dates WHERE dt + ? <= CURRENT_TIMESTAMP() ) SELECT dates.dt date, COALESCE(t.total, 0) AS total FROM all_dates dates LEFT JOIN (SELECT DATE(time) AS date, COUNT(time) AS total FROM hypercube.support_sessions GROUP BY DATE(time)) t ON t.date = dates.dt ORDER BY dates.dt",
                    (statement) -> {
                        statement.setDate(1, sqlDate);
                        statement.setDate(2, sqlDate);
                    })
                )
                .compile()
                .run((query) -> {
                    Map<GraphableEntry<?>, Integer> dates = new LinkedHashMap<>();
                    int i = 0;
                    for (ResultSet set : query) {
                        String timedisplay = i+"";
                        if(i < 10){
                            timedisplay = "0" + timedisplay;
                        }
                        dates.put(new StringEntry(timedisplay + ":00"), set.getInt("total"));
                        i++;
                    }
                    
                    event.getChannel().sendFile(new ChartGraphBuilder()
                            .setGraphName(String.format("Total sessions on %s", date))
                            .createGraph(dates)).queue();
                    
                });
        
    }
    
}
