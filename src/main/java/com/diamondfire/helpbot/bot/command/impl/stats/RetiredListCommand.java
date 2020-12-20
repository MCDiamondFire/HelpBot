package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.reactions.multiselector.MultiSelectorBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
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
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
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
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM ranks, players WHERE ranks.uuid = players.uuid " +
                        "AND ranks.retirement > 0 " +
                        "AND ranks.moderation = 0 " +
                        "AND ranks.support = 0"))
                .compile()
                .run((result) -> {
                    Map<Rank, List<String>> retiredList = new HashMap<>();
                    retiredList.put(Rank.RETIRED, new ArrayList<>());
                    retiredList.put(Rank.EMERITUS, new ArrayList<>());
                    
                    for (ResultSet set : result) {
                        retiredList.get(Rank.fromBranch(RankBranch.RETIREMENT, set.getInt("retirement"))).add(StringUtil.display(set.getString("name")));
                    }
                    
                    EmbedBuilder retired = new EmbedBuilder();
                    EmbedUtil.addFields(retired, retiredList.get(Rank.RETIRED), "", "", true);
                    EmbedBuilder emeritus = new EmbedBuilder();
                    EmbedUtil.addFields(emeritus, retiredList.get(Rank.EMERITUS), "", "", true);
                    
                    builder.addPage("Retired", retired);
                    builder.addPage("Emeritus", emeritus);
                });
        builder.build().send(event.getJDA());
    }
    
}


