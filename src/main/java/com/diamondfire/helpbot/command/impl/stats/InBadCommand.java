package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.LimitedIntegerArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
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
    public String getDescription() {
        return "Gets current staff members who have not done a session in 30 days.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.STATS;
    }

    @Override
    public ValueArgument<Integer> getArgument() {
        return new LimitedIntegerArg("Session Count", 5, Integer.MAX_VALUE, 5);
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        int num = getArgument().getArg(event.getParsedArgs());

        new SingleQueryBuilder()
                .query("SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0")
                .onQuery((resultTable) -> {
                    List<String> staff = new ArrayList<>();
                    do {
                        staff.add(StringUtil.display(resultTable.getString("name")));
                    } while (resultTable.next());

                    new SingleQueryBuilder()
                            .query("SELECT DISTINCT staff, COUNT(*) as session FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL 30 DAY GROUP BY staff HAVING COUNT(staff) >= ?", (statement -> {
                                statement.setInt(1, num);
                            }))
                            .onQuery((resultBadTable) -> {
                                do {
                                    staff.remove(StringUtil.display(resultBadTable.getString("staff")));
                                } while (resultBadTable.next());

                                builder.setTitle(String.format("People with less than %s sessions:", num));
                                builder.setColor(Color.RED);
                                builder.setDescription(String.join("\n", staff.toArray(new String[0])));
                                event.getChannel().sendMessage(builder.build()).queue();
                            }).execute();
                }).execute();

    }
}
