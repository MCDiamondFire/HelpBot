package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;

public class PlotsCommand extends AbstractPlayerCommand {

    @Override
    public String getName() {
        return "plots";
    }

    @Override
    public String getDescription() {
        return "Gets current plots owned by user.";
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        EmbedBuilder builder = new EmbedBuilder();
        new SingleQueryBuilder()
                .query("SELECT * FROM plots WHERE owner_name = ? OR owner = ? LIMIT 25;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery((resultTablePlot) -> {
                    do {
                        ArrayList<String> stats = new ArrayList<>();
                        builder.setTitle(resultTablePlot.getString("owner_name") + "'s Plots");
                        stats.add("Votes: " + resultTablePlot.getInt("votes"));
                        stats.add("Players: " + resultTablePlot.getInt("player_count"));

                        builder.addField(StringUtil.stripColorCodes(resultTablePlot.getString("name")) +
                                        String.format(" **(%s)**", resultTablePlot.getInt("id")),
                                String.join("\n", stats), false);

                    } while (resultTablePlot.next());
                })
                .onNotFound(() -> {
                    builder.setTitle("Error!");
                    builder.setDescription("Player was not found");
                }).execute();
        event.getChannel().sendMessage(builder.build()).queue();

    }


}

