package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.DateEntry;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;

import java.sql.ResultSet;
import java.util.ArrayList;


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
                .query(new BasicQuery("SELECT * FROM support_sessions WHERE staff = ? ORDER BY time", (statement) -> statement.setString(1, player)))
                .compile()
                .run((query) -> {
                    ArrayList<DateEntry> dates = new ArrayList<>();
                    for (ResultSet set : query) {
                        dates.add(new DateEntry(set.getDate("time")));
                    }

                    event.getChannel().sendFile(new ChartGraphBuilder()
                            .setGraphName(String.format("%s's sessions", player))
                            .createGraphFromCollection(dates)).queue();

                });

    }

}
