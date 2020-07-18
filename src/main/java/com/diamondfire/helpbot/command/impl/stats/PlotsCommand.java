package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class PlotsCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "plots";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ownedplots"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current plots owned by a certain user. (Max of 25)")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
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
                    builder.setTitle(resultTablePlot.getString("owner_name") + "'s Plots");
                    do {
                        String[] stats = {
                                "Votes: " + resultTablePlot.getInt("votes"),
                                "Players: " + resultTablePlot.getInt("player_count")
                        };
                        builder.addField(StringUtil.display(resultTablePlot.getString("name")) +
                                        String.format(" **(%s)**", resultTablePlot.getInt("id")),
                                String.join("\n", stats), false);

                    } while (resultTablePlot.next());
                })
                .onNotFound(() -> {
                    builder.setTitle("Error!");
                    builder.setDescription("Player was not found, or they have no plots.");
                }).execute();
        event.getChannel().sendMessage(builder.build()).queue();
    }

}

