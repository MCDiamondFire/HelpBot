package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.bot.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.df.ranks.Ranks;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

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

                    EmbedBuilder retired = new EmbedBuilder();
                    Util.addFields(retired, retiredList.get(Ranks.RETIRED.getNumber()), "", "");
                    EmbedBuilder emeritus = new EmbedBuilder();
                    Util.addFields(emeritus, retiredList.get(Ranks.EMERITUS.getNumber()), "", "");

                    builder.addPage("Retired", retired);
                    builder.addPage("Emeritus", emeritus);
                }).execute();
        builder.build().send(event.getJDA());

    }

}


