package com.diamondfire.helpbot.bot.command.impl.other;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.io.*;
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
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("query", new MultiArgumentContainer<>(new StringArgument()));
    }

    @Override
    public Permission getPermission() {
        return Permission.BOT_DEVELOPER;
    }

    @Override
    public void run(CommandEvent event) {
        List<String> args = event.getArgument("query");
        String query = String.join(" ", args);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("SQL Result");

        new SingleQueryBuilder().query(query)
                .onQuery((set) -> {
                    int width = set.getMetaData().getColumnCount();
                    List<String> objects = new ArrayList<>();
                    do {
                        HashMap<String, String> entries = new HashMap<>();
                        for (int i = 1; i <= width; i++) {
                            String columnName = set.getMetaData().getColumnName(i);
                            entries.put(columnName, set.getObject(i).toString());
                        }
                        objects.add(StringUtil.asciidocStyle(entries));
                    } while (set.next());

                    for (int i = 0; i < objects.size(); i++) {
                        builder.addField("Row: " + (i + 1), String.format("```asciidoc\n%s```", objects.get(i)), false);
                    }
                    event.getChannel().sendMessage(builder.build()).queue();
                })
                .onException((e) -> {
                    e.printStackTrace();
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String sStackTrace = sw.toString();
                    builder.setTitle("Query failed!");
                    event.getChannel().sendMessage(builder.build()).queue();
                    event.getChannel().sendMessage(String.format("```%s```", sStackTrace.length() >= 1500 ? sStackTrace.substring(0, 1500) : sStackTrace)).queue();

                }).execute();

    }

}
