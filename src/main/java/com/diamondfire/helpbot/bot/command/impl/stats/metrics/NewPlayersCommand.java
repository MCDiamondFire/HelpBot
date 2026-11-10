package com.diamondfire.helpbot.bot.command.impl.stats.metrics;

import com.diamondfire.helpbot.util.*;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.utils.TimeFormat;

import java.sql.ResultSet;
import java.util.*;

public class NewPlayersCommand extends Command {
    
    @Override
    public String getName() {
        return "newplayers";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the first 25 new players who have joined in the last 24 hours.")
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
        List<String> lines = new ArrayList<>();
        int[] total = new int[1];
        new DatabaseQuery()
                .query(new BasicQuery("SELECT name, join_date, (SELECT COUNT(*) FROM players WHERE join_date > CURRENT_TIMESTAMP() - INTERVAL 1 DAY) AS total " +
                        "FROM players WHERE join_date > CURRENT_TIMESTAMP() - INTERVAL 1 DAY ORDER BY join_date DESC LIMIT 20"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        lines.add(StringUtil.escapeName(set.getString("name")) + " - joined " +
                                TimeFormat.RELATIVE.format(set.getTimestamp("join_date").getTime()));
                        total[0] = set.getInt("total");
                    }
                });
        
        String description;
        if (lines.isEmpty()) {
            description = "No new players in the last 24 hours.";
        } else {
            description = String.join("\n", lines);
            if (total[0] > lines.size()) {
                description += String.format("\n… and %s more", FormatUtil.formatNumber(total[0] - lines.size()));
            }
        }
        
        event.reply(new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "New Players, Last 24 Hours", description)
                ));
    }
    
}
