package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.argument.impl.types.*;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.graph.graphable.*;
import com.diamondfire.helpbot.components.graph.impl.ChartGraphBuilder;
import com.diamondfire.helpbot.events.CommandEvent;

import java.util.ArrayList;

public class PlayerJoinGraphCommand extends Command {

    @Override
    public String getName() {
        return "playergraph";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph representing how many players joined in a certain timeframe (unique).")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("daily|weekly|monthly"),
                        new HelpContextArgument()
                                .name("amount")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("mode",
                        new DefinedStringArgument("daily", "weekly", "monthly"))
                .addArgument("amount",
                        new ClampedIntegerArgument(1, 99999999));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        ArrayList<GraphableEntry<?>> entries = new ArrayList<>();
        String mode = event.getArgument("mode");
        int amount = event.getArgument("amount");
        switch (mode) {
            case "daily":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%d') AS time " +
                                "FROM player_join_log WHERE time > DATE_FORMAT(CURRENT_TIMESTAMP - INTERVAL ? DAY, '%y-%m-%d')) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                for (int i = 0; i < resultTable.getInt("count") + 1; i++) {
                                    entries.add(new StringEntry(resultTable.getString("time")));
                                }

                            } while (resultTable.next());

                            event.getChannel().sendFile(new ChartGraphBuilder()
                                    .setGraphName("Unique player joins per day")
                                    .createGraph(entries)).queue();
                        }).execute();
                break;
            case "weekly":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%d') AS time " +
                                "FROM player_join_log WHERE time > DATE_FORMAT(CURRENT_TIMESTAMP - INTERVAL ? WEEK, '%y-%m-%d')) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                for (int i = 0; i < resultTable.getInt("count") + 1; i++) {
                                    entries.add(new StringEntry(resultTable.getString("time")));
                                }

                            } while (resultTable.next());

                            event.getChannel().sendFile(new ChartGraphBuilder()
                                    .setGraphName("Unique player joins per week")
                                    .createGraph(entries)).queue();
                        }).execute();
                break;
            case "monthly":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m') AS time FROM player_join_log " +
                                        "WHERE time > DATE_FORMAT(CURRENT_TIMESTAMP - INTERVAL ? MONTH, '%y-%m-%d')) a GROUP BY time;"
                                , statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                for (int i = 0; i < resultTable.getInt("count") + 1; i++) {
                                    entries.add(new StringEntry(resultTable.getString("time")));
                                }

                            } while (resultTable.next());

                            event.getChannel().sendFile(new ChartGraphBuilder()
                                    .setGraphName("Unique player joins per month")
                                    .createGraph(entries)).queue();
                        }).execute();
                break;

        }

    }

}


