package com.diamondfire.helpbot.bot.command.impl.other.dev;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MessageArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.*;
import java.sql.ResultSet;
import java.util.*;


public class QueryCommand extends Command {
    
    @Override
    public String getName() {
        return "query";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Executes given query.")
                .category(CommandCategory.OTHER)
                .addArgument(
                        new HelpContextArgument()
                                .name("query")
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("query", new MessageArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }
    
    @SuppressWarnings("LanguageMismatch")
    @Override
    public void run(CommandEvent event) {
        String query = event.getArgument("query");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("SQL Result");
        
        try {
            new DatabaseQuery()
                    .query(new BasicQuery(query))
                    .compile()
                    .run((set) -> {
                        int width = set.getResult().getMetaData().getColumnCount();
                        List<String> objects = new ArrayList<>();
                        for (ResultSet resultSet : set) {
                            HashMap<String, String> entries = new HashMap<>();
                            for (int i = 1; i <= width; i++) {
                                String columnName = resultSet.getMetaData().getColumnName(i);
                                entries.put(columnName, String.valueOf(resultSet.getObject(i)));
                            }
                            objects.add(StringUtil.asciidocStyle(entries));
                        }
                        
                        for (int i = 0; i < objects.size(); i++) {
                            if (i > 25) {
                                break;
                            }
                            builder.addField("Row: " + (i + 1), String.format("```asciidoc\n%s```", objects.get(i)), false);
                        }
                        
                        event.getReplyHandler().reply(builder);
                    });
        } catch (IllegalStateException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String sStackTrace = sw.toString();
            builder.setTitle("Query failed! " + e.getClass().getName());
            event.getChannel().sendMessage(builder.build()).queue();
            event.getChannel().sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();
        }
        
    }
    
}
