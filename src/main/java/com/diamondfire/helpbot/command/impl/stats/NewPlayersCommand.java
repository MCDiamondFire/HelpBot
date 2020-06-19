package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.impl.CommandCategory;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class NewPlayersCommand extends Command {

    @Override
    public String getName() {
        return "newplayers";
    }

    @Override
    public String getDescription() {
        return "Gets the new players who have joined today.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.STATS;
    }

    @Override
    public Argument getArgument() {
        return new NoArg();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Players who have joined in last 24 hours");
        new SingleQueryBuilder()
                .query("SELECT players.name, approved_users.time FROM approved_users " +
                        "LEFT JOIN players ON approved_users.uuid = players.uuid " +
                        "WHERE time > CURRENT_TIMESTAMP() - INTERVAL 1 DAY ORDER BY time DESC LIMIT 20")
                .onQuery((resultTable) -> {
                    do {
                        builder.addField(StringUtil.display(resultTable.getString("name")), resultTable.getTimestamp("time").toString(), false);
                    } while (resultTable.next());
                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();

    }

}


