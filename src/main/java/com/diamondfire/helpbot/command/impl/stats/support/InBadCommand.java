package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.command.help.CommandCategory;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.help.HelpContextArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InBadCommand extends Command {

    @Override
    public String getName() {
        return "bad";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current staff members who have not done a certain number of sessions in 30 days.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("sessions")
                                .optional()
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("count",
                        new ClampedIntegerArgument(1, Integer.MAX_VALUE).optional(5));
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    public void run(CommandEvent event) {
        int num = event.getArgument("count");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("People with less than %s sessions this month:", num));
        builder.setColor(Color.RED);

        new SingleQueryBuilder()
                .query("SELECT * FROM (SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0 AND ranks.developer IS NULL) a " +
                                "WHERE name NOT IN (SELECT DISTINCT staff AS name FROM hypercube.support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL 30 DAY GROUP BY staff HAVING COUNT(staff) >= ?)",
                        (statement -> statement.setInt(1, num)))
                .onQuery((resultTable) -> {
                    List<String> staff = new ArrayList<>();
                    do {
                        staff.add(StringUtil.display(resultTable.getString("name")));
                    } while (resultTable.next());


                    Util.addFields(builder, staff, "", "");
                    event.getChannel().sendMessage(builder.build()).queue();
                })
                .onNotFound(() -> {
                    builder.setDescription("");
                    event.getChannel().sendMessage(builder.build()).queue();
                }).execute();
    }
}
