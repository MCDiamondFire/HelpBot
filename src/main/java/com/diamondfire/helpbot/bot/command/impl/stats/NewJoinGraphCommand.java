package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;

public class NewJoinGraphCommand extends Command {

    public static void generateGraph(String mode, int amount, TextChannel channel) {
        Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
        ChartGraphBuilder builder = new ChartGraphBuilder();

        switch (mode) {
            case "daily":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%d') AS time " +
                                "FROM hypercube.approved_users WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                entries.put(new StringEntry(resultTable.getString("time")), resultTable.getInt("count"));
                            } while (resultTable.next());

                            builder.setGraphName("New players per day");
                        }).execute();
                break;
            case "weekly":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%v') AS time " +
                                "FROM hypercube.approved_users WHERE time > CURRENT_TIMESTAMP - INTERVAL ? WEEK AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                entries.put(new StringEntry(resultTable.getString("time")), resultTable.getInt("count"));
                            } while (resultTable.next());

                            builder.setGraphName("New players per week");
                        }).execute();
                break;
            case "monthly":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m') AS time " +
                                "FROM hypercube.approved_users WHERE time > CURRENT_TIMESTAMP - INTERVAL ? MONTH AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                do {
                                    entries.put(new StringEntry(resultTable.getString("time")), resultTable.getInt("count"));
                                } while (resultTable.next());


                            } while (resultTable.next());

                            builder.setGraphName("New players per month");
                        }).execute();
                break;

        }

        channel.sendFile(builder.createGraph(entries)).queue();
    }

    @Override
    public String getName() {
        return "joingraph";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph representing how many new players joined in a certain time frame.")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("daily|amount|monthly"),
                        new HelpContextArgument()
                                .name("amount")
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("mode",
                        new DefinedObjectArgument("daily", "weekly", "monthly"))
                .addArgument("amount",
                        new ClampedIntegerArgument(1));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        String mode = event.getArgument("mode");
        int amount = event.getArgument("amount");

        generateGraph(mode, amount, event.getChannel());
    }

}


