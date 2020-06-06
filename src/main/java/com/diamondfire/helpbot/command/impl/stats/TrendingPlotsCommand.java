package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;

public class TrendingPlotsCommand extends Command {

    @Override
    public String getName() {
        return "trending";
    }

    @Override
    public String getDescription() {
        return "Gets current trending plots.";
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Trending Plots");
        new SingleQueryBuilder()
                .query("SELECT * FROM plots WHERE whitelist = 0 ORDER BY votes DESC LIMIT 10")
                .onQuery((resultTable) -> {
                    do {
                        ArrayList<String> stats = new ArrayList<>();
                        stats.add("Votes: " + resultTable.getInt("votes"));

                        int count = resultTable.getInt("player_count");
                        if (count > 0) {
                           stats.add("Players: " + count);
                        }

                        builder.addField(StringUtil.stripColorCodes(resultTable.getString("name")) +
                                        String.format(" **(%s)**", resultTable.getInt("id")),
                                String.join("\n", stats), false);
                    } while (resultTable.next());
                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();


    }


}


