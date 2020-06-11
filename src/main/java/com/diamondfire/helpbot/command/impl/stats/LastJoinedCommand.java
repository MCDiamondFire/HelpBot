package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Date;

public class LastJoinedCommand extends AbstractPlayerCommand {

    @Override
    public String getName() {
        return "lastjoined";
    }

    @Override
    public String getDescription() {
        return "Gets the time when someone last joined.";
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
                                builder.setTitle(player);
                                builder.addField("Last Seen", StringUtil.formatDate(resultTableDate.getDate("time")), false);
                            })
                            .onNotFound(() -> {
                                builder.setTitle(player);
                                builder.addField("Last Seen", "A long time ago...", false);
                            }).execute();
                })
                .onNotFound(() -> {
                    builder.setTitle("Player not found!");
                }
        ).execute();

        event.getChannel().sendMessage(builder.build()).queue();


    }

}
