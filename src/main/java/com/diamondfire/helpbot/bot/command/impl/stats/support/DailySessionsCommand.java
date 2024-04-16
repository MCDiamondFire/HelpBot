package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.DateArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.util.DateUtil;
import net.dv8tion.jda.api.utils.FileUpload;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;


public class DailySessionsCommand extends Command {
    
    @Override
    public String getName() {
        return "dailysessions";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"ds"};
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
                        new SingleArgumentContainer<>(new DateArgument()).optional(null));
        
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }
    
    @Override
    public void run(CommandEvent event) {
        Date date;
        if (event.getArgument("date") == null) {
            date = Date.from(Instant.now());
        } else {
            date = event.getArgument("date");
        }
        
        java.sql.Date sqlDate = DateUtil.toSqlDate(date);
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT hour(time) AS time, COUNT(*) AS count\n" +
                        "FROM support_sessions\n" +
                        "WHERE DATE(time) = ?\n" +
                        "GROUP BY hour(time)\n" +
                        "ORDER BY time",
                        (statement) -> statement.setDate(1, sqlDate))
                )
                .compile()
                .run((query) -> {
                    Map<GraphableEntry<?>, Integer> dates = new LinkedHashMap<>();
                    ResultSet set = query.getResult();
                    
                    {
                        Map<Integer, Integer> entries = new LinkedHashMap<>();
                        for (int i = 1; i < 25; i++) {
                            entries.put(i, 0);
                        }
                        
                        while (set.next()) {
                            entries.put(set.getInt("time"), set.getInt("count"));
                        }
                        
                        for (Map.Entry<Integer, Integer> entry : entries.entrySet()) {
                            int hour = entry.getKey(); // 1-24
                            String timedisplay = hour + "";
                            if (hour < 10) {
                                timedisplay = "0" + timedisplay;
                            }
                            
                            dates.put(new StringEntry(timedisplay + ":00"), entry.getValue());
                        }
                    }
                    
                    event.getChannel().sendFiles(FileUpload.fromData(new ChartGraphBuilder()
                            .setGraphName(String.format("Total sessions on %s", date))
                            .createGraph(dates))).queue();
                    
                });
        
    }
    
}