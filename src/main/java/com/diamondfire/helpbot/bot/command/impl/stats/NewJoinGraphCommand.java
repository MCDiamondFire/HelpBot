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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class NewJoinGraphCommand extends Command {

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

    public static void generateGraph(String mode, int amount, TextChannel channel) {
        ArrayList<GraphableEntry<?>> entries = new ArrayList<>();
        AtomicReference<ChartGraphBuilder> builder = new AtomicReference<>();

        switch (mode) {
            case "daily":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%d') AS time " +
                                "FROM approved_users WHERE time > DATE_FORMAT(CURRENT_TIMESTAMP - INTERVAL ? DAY, '%y-%m-%d') AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                for (int i = 0; i < resultTable.getInt("count") + 1; i++) {
                                    entries.add(new StringEntry(resultTable.getString("time")));
                                }

                            } while (resultTable.next());

                            builder.set(new ChartGraphBuilder()
                                    .setGraphName("New players per day"));
                        }).execute();
                break;
            case "weekly":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%d') AS time " +
                                "FROM approved_users WHERE time > DATE_FORMAT(CURRENT_TIMESTAMP - INTERVAL ? WEEK , '%y-%m-%d') AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                                "GROUP BY time;", statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                for (int i = 0; i < resultTable.getInt("count") + 1; i++) {
                                    entries.add(new StringEntry(resultTable.getString("time")));
                                }

                            } while (resultTable.next());

                            builder.set(new ChartGraphBuilder()
                                    .setGraphName("New players per week"));
                        }).execute();
                break;
            case "monthly":
                new SingleQueryBuilder()
                        .query("SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m') AS time FROM approved_users " +
                                        "WHERE time > DATE_FORMAT(CURRENT_TIMESTAMP - INTERVAL ? MONTH, '%y-%m-%d') AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a GROUP BY time;"
                                , statement -> statement.setInt(1, amount))
                        .onQuery((resultTable) -> {
                            do {
                                for (int i = 0; i < resultTable.getInt("count") + 1; i++) {
                                    entries.add(new StringEntry(resultTable.getString("time")));
                                }

                            } while (resultTable.next());

                            builder.set(new ChartGraphBuilder()
                                    .setGraphName("New players per month"));
                        }).execute();
                break;

        }

        channel.sendFile(builder.get().createGraph(entries)).queue();
    }

}


