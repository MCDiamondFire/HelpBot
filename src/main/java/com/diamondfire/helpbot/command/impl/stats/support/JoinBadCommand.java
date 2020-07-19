package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.*;

public class JoinBadCommand extends Command {

    @Override
    public String getName() {
        return "joinbad";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current staff members who have not joined in a certain number of days.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("days")
                                .optional()
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("days",
                        new ClampedIntegerArgument(2, 100).optional(30));
    }

    @Override
    public Permission getPermission() {
        return Permission.EXPERT;
    }

    @Override
    public void run(CommandEvent event) {

        int num = event.getArgument("days");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("Staff who have not joined in %s days:", num));
        builder.setColor(Color.RED);
        new SingleQueryBuilder()
                .query("SELECT DISTINCT uuid,name FROM" +
                        "(SELECT players.name, players.uuid FROM ranks,players WHERE ranks.uuid = players.uuid AND ranks.support > 0 | ranks.moderation > 0) a " +
                        "WHERE uuid NOT IN" +
                        "(SELECT DISTINCT uuid from  player_join_log where time > CURRENT_TIMESTAMP - INTERVAL ? DAY)", statement -> {
                    statement.setInt(1, num);
                })
                .onQuery((resultTableJoins) -> {
                    List<String> staff = new ArrayList<>();
                    do {
                        staff.add(resultTableJoins.getString("name"));
                    } while (resultTableJoins.next());

                    Util.addFields(builder, staff, "", "", true);
                    event.getChannel().sendMessage(builder.build()).queue();

                })
                .onNotFound(() -> {
                    builder.setDescription("");
                    event.getChannel().sendMessage(builder.build()).queue();
                }).execute();

    }
}
