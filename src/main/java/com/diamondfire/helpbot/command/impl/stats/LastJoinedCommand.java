package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.help.CommandCategory;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class LastJoinedCommand extends AbstractPlayerUUIDCommand {

    @Override
    public String getName() {
        return "lastjoined";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"lastseen"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the last date when a user joined.")
                .category(CommandCategory.STATS)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }

    @Override
    protected void execute(CommandEvent event, String player) {
        EmbedBuilder builder = new EmbedBuilder();
        new SingleQueryBuilder()
                .query("SELECT players.uuid FROM players WHERE players.uuid = ? OR players.name = ?;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                })
                .onQuery((resultTable) -> {
                    new SingleQueryBuilder()
                            .query("SELECT time FROM player_join_log WHERE uuid = ? ORDER BY time DESC LIMIT 1;", (statement) -> {
                                statement.setString(1, resultTable.getString("uuid"));
                            })
                            .onQuery((resultTableDate) -> {
                                builder.setAuthor(StringUtil.display(player), null, "https://mc-heads.net/head/" + player);
                                builder.addField("Last Seen", StringUtil.formatDate(resultTableDate.getDate("time")), false);
                            })
                            .onNotFound(() -> {
                                builder.setAuthor(StringUtil.display(player), null, "https://mc-heads.net/head/" + player);
                                builder.addField("Last Seen", "A long time ago...", false);
                            }).execute();
                })
                .onNotFound(() -> builder.setTitle("Player not found!")).execute();

        event.getChannel().sendMessage(builder.build()).queue();
    }

}
