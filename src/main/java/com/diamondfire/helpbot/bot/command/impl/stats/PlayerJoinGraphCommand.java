package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.graph.graphable.*;
import com.diamondfire.helpbot.sys.graph.impl.ChartGraphBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.intellij.lang.annotations.Language;

import java.io.File;
import java.sql.ResultSet;
import java.util.*;

public class PlayerJoinGraphCommand extends Command {

    public static void generateGraph(String mode, int amount, TextChannel channel) {
        File graph = null;
        switch (mode) {
            case "daily":
                graph = makeGraph("Unique player joins per day", "SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%d') AS time " +
                        "FROM hypercube.player_join_log WHERE time > CURRENT_DATE() - INTERVAL ? DAY AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                        "GROUP BY time;", amount);
                break;
            case "weekly":
                graph = makeGraph("Unique player joins per week", "SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m-%v') AS time " +
                        "FROM hypercube.player_join_log WHERE time > CURRENT_DATE() - INTERVAL ? WEEK AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                        "GROUP BY time;", amount);
                break;
            case "monthly":
                graph = makeGraph("Unique player joins per month", "SELECT time, COUNT(*) AS count FROM (SELECT DISTINCT uuid, DATE_FORMAT(time, '%y-%m') AS time " +
                        "FROM hypercube.player_join_log WHERE time > CURRENT_DATE() - INTERVAL ? MONTH AND uuid NOT IN (SELECT uuid FROM litebans.bans WHERE active = 1 AND until = -1)) a " +
                        "GROUP BY time;", amount);
                break;
        }

        channel.sendFile(graph).queue();
    }

    @Override
    public String getName() {
        return "playergraph";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a graph representing how many players joined in a certain time frame (unique).")
                .category(CommandCategory.GENERAL_STATS)
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
                        new DefinedObjectArgument<>("daily", "weekly", "monthly"))
                .addArgument("amount",
                        new ClampedIntegerArgument(1, 99999999));
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

    private static File makeGraph(String title, @Language("SQL") String query, int amt) {
        Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
        ChartGraphBuilder builder = new ChartGraphBuilder();

        new DatabaseQuery()
                .query(new BasicQuery(query, statement -> statement.setInt(1, amt)))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        entries.put(new StringEntry(set.getString("time")), set.getInt("count"));
                    }

                    builder.setGraphName(title);
                });

        return builder.createGraph(entries);
    }
}


