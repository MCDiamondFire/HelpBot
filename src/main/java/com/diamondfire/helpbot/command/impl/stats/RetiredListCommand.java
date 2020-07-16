package com.diamondfire.helpbot.command.impl.stats;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.CommandCategory;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.components.dfranks.Ranks;
import com.diamondfire.helpbot.components.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetiredListCommand extends Command {

    @Override
    public String getName() {
        return "retiredlist";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"retired", "retiredstaff"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current retired staff members.")
                .category(CommandCategory.STATS);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
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
                .query("SELECT * FROM ranks, players WHERE ranks.uuid = players.uuid " +
                        "AND ranks.retirement > 0 " +
                        "AND ranks.moderation = 0 " +
                        "AND ranks.support = 0")
                .onQuery((resultTable) -> {
                    Map<Integer, List<String>> retiredList = new HashMap<>();
                    retiredList.put(Ranks.RETIRED.getNumber(), new ArrayList<>());
                    retiredList.put(Ranks.EMERITUS.getNumber(), new ArrayList<>());

                    do {
                        retiredList.get(resultTable.getInt("retirement")).add(StringUtil.display(resultTable.getString("name")));
                    } while (resultTable.next());

                    builder.addPage("Retired", Util.addFields(new EmbedBuilder(), retiredList.get(Ranks.RETIRED.getNumber()), "", ""));
                    builder.addPage("Emeritus", Util.addFields(new EmbedBuilder(), retiredList.get(Ranks.EMERITUS.getNumber()), "", ""));
                }).execute();
        builder.build().send();

    }

}


