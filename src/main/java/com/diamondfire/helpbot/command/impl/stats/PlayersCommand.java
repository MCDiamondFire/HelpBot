package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.CommandCategory;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayersCommand extends Command {

    @Override
    public String getName() {
        return "players";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"currentplayers", "playerc", "nodes", "playerlist", "nodelist", "nodestatus", "count"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current player count on each node.")
                .category(CommandCategory.STATS);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Current Players:");
        AtomicInteger totalPlayers = new AtomicInteger();
        new SingleQueryBuilder()
                .query("SELECT * FROM node_info")
                .onQuery((resultTable) -> {
                    do {
                        int nodeCount = resultTable.getInt("player_count");
                        builder.addField("Node " + resultTable.getInt("node"), "Players: " + nodeCount, false);
                        totalPlayers.addAndGet(nodeCount);
                    } while (resultTable.next());
                    new SingleQueryBuilder()
                            .query("SELECT * FROM hypercube_beta.node_info")
                            .onQuery((resultTableBeta) -> {
                                do {
                                    int nodeCount = resultTableBeta.getInt("player_count");
                                    builder.addField("Node Beta", "Players: " + nodeCount, false);
                                    totalPlayers.addAndGet(nodeCount);
                                } while (resultTableBeta.next());
                                builder.addField("All Nodes", "Players: " + totalPlayers, false);

                                if (totalPlayers.get() == 0) {
                                    builder.addField(":rotating_light: DF BROKE :rotating_light:", "Jere, fix it already! >:(", false);
                                }
                                event.getChannel().sendMessage(builder.build()).queue();
                            }).execute();
                }).execute();

    }

}


