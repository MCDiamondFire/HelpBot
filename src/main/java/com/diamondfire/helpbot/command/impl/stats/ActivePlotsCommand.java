package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class ActivePlotsCommand extends Command {

    @Override
    public String getName() {
        return "activeplots";
    }

    @Override
    public String getDescription() {
        return "Gets current active plots.";
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
        builder.setTitle("Active Plots");
        new SingleQueryBuilder()
                .query("SELECT * FROM plots WHERE player_count > 0 AND whitelist = 0 ORDER BY player_count DESC LIMIT 10")
                .onQuery((resultTable) -> {
                    do {
                        builder.addField(StringUtil.stripColorCodes(resultTable.getString("name")) +
                                String.format(" **(%s)**", resultTable.getInt("id")),
                                "Players: " + resultTable.getInt("player_count"), false);
                    } while (resultTable.next());
                }).execute();

        event.getChannel().sendMessage(builder.build()).queue();

    }


}


