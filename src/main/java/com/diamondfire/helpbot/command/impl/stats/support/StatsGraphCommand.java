package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.graph.graphable.DateEntry;
import com.diamondfire.helpbot.components.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.events.CommandEvent;

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
        new SingleQueryBuilder()
                .query("SELECT * FROM support_sessions WHERE staff = ? ORDER BY time", (statement) -> {
                    statement.setString(1, player);
                })
                .onQuery((query) -> {
                    ArrayList<DateEntry> dates = new ArrayList<>();
                    do {
                        dates.add(new DateEntry(query.getDate("time")));
                    } while (query.next());

                    event.getChannel().sendFile(new ChartGraphBuilder()
                            .setGraphName(String.format("%s's sessions", player))
                            .createGraph(dates)).queue();

                }).execute();

    }

}
