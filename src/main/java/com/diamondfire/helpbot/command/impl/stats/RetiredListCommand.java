package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.arguments.NoArg;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;

public class RetiredListCommand extends Command {

    @Override
    public String getName() {
        return "retiredlist";
    }

    @Override
    public String getDescription() {
        return "Gets current retired staff members.";
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
        MultiSelectorBuilder builder = new MultiSelectorBuilder();
        builder.setChannel(event.getChannel().getIdLong());
        builder.setUser(event.getMember().getIdLong());

        new SingleQueryBuilder()
                .query("SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.retirement = 1 AND ranks.moderation = 0 AND ranks.support = 0")
                .onQuery((resultTable) -> {
                    ArrayList<String> retired = new ArrayList<>();
                    do {
                        retired.add(resultTable.getString("name"));
                    } while (resultTable.next());
                    builder.addPage("Retired", Util.addFields(new EmbedBuilder(), retired, "", ""));
                }).execute();
        new SingleQueryBuilder()
                .query("SELECT players.name FROM ranks, players WHERE ranks.uuid = players.uuid AND ranks.retirement = 2 AND ranks.moderation = 0 AND ranks.support = 0")
                .onQuery((resultTable) -> {
                    ArrayList<String> retired = new ArrayList<>();
                    do {
                        retired.add(resultTable.getString("name"));
                    } while (resultTable.next());
                    builder.addPage("Emeritus", Util.addFields(new EmbedBuilder(), retired, "", ""));
                }).execute();
        builder.build().send();

    }


}


