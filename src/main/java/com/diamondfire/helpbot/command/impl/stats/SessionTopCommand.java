package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.value.LimitedIntegerArg;
import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.*;

public class SessionTopCommand extends Command {

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getDescription() {
        return "Gets people with the top sessions.";
    }

    @Override
    public ValueArgument<Integer> getArgument() {
        return new LimitedIntegerArg("days", 1, Integer.MAX_VALUE, 30);
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        int days = getArgument().getArg(event.getParsedArgs());
        builder.setTitle(String.format("Top Support Members in %s days", days));

        new SingleQueryBuilder()
                .query("SELECT DISTINCT staff, COUNT(*) as sessions FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff ORDER BY sessions DESC LIMIT 10", (statement) -> {
                    statement.setInt(1, days);
                })
                .onQuery((resultTable) -> {
                    LinkedHashMap<String, Integer> stats = new LinkedHashMap<>();
                    do {
                        stats.put(resultTable.getString("staff"), resultTable.getInt("sessions"));
                    } while (resultTable.next());

                    for (Map.Entry<String, Integer> stat : stats.entrySet()) {
                        builder.addField(MarkdownSanitizer.escape(stat.getKey()), "\nSessions: " + stat.getValue(), false);
                    }

                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();


    }


}


