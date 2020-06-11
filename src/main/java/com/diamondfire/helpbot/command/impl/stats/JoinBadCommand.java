package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.LimitedIntegerArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;

public class JoinBadCommand extends Command {

    @Override
    public String getName() {
        return "joinbad";
    }

    @Override
    public String getDescription() {
        return "Gets current staff members who have not joined in 30 days.";
    }

    @Override
    public ValueArgument<Integer> getArgument() {
        return new LimitedIntegerArg("Days", 2, 100, 30);
    }

    @Override
    public Permission getPermission() {
        return Permission.EXPERT;
    }

    @Override
    public void run(CommandEvent event) {

        // I recommend not trying to improve this unless you know what you are doing.
        // I managed to kill the DF database completely by using my awful SQL knowledge.

        EmbedBuilder builder = new EmbedBuilder();
        int num = getArgument().getArg(event.getParsedArgs());

        new SingleQueryBuilder()
                .query("SELECT players.name, players.uuid FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0")
                .onQuery((resultTable) -> {
                    HashMap<String, String> staff = new HashMap<>();
                    do {
                        staff.put(resultTable.getString("uuid"), resultTable.getString("name"));
                    } while (resultTable.next());

                    new SingleQueryBuilder()
                            .query("SELECT DISTINCT uuid FROM player_join_log WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY ", statement -> {
                                statement.setInt(1, num);
                            })
                            .onQuery((resultTableJoins) -> {
                                do {
                                    staff.remove(resultTableJoins.getString("uuid"));
                                } while (resultTableJoins.next());

                                builder.setTitle(String.format("Staff who have not joined in %s days:", num));
                                builder.setColor(Color.RED);
                                builder.setDescription(String.join("\n", staff.values().toArray(new String[0])));
                                event.getChannel().sendMessage(builder.build()).queue();

                            }).execute();
                }).execute();


    }
}
