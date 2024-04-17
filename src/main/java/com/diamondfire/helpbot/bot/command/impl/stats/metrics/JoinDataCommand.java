package com.diamondfire.helpbot.bot.command.impl.stats.metrics;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.df.ranks.Rank;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.util.*;

public class JoinDataCommand extends Command {
    
    @Override
    public String getName() {
        return "joindata";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Shows current join data within a set amount of time.")
                .addArgument(
                        new HelpContextArgument()
                                .name("date"),
                        new HelpContextArgument()
                                .name("days")
                                .optional(),
                        new HelpContextArgument()
                                .name("join again date")
                                .optional(),
                        new HelpContextArgument()
                                .name("join again days")
                                .optional()
                )
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("date",
                        new DateArgument())
                .addArgument("days",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(1)).optional(7))
                .addArgument("daterejoin",
                        new SingleArgumentContainer<>(new DateArgument()).optional(null))
                .addArgument("daysrejoin",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(1)).optional(null));
        
    }
    
    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        EmbedBuilder embed = builder.getEmbed();
        builder.withPreset(
                new InformativeReply(InformativeReplyType.INFO, "Join Statistics", null)
        );
        
        int days = event.getArgument("days");
        Date date = event.getArgument("date");
        java.sql.Date sqlDate = DateUtil.toSqlDate(date);
        
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        
        java.sql.Date sqlDateTo = DateUtil.toSqlDate(c.getTime());
        
        // convert calendar to date
        String dateFrom = FormatUtil.formatDate(date);
        String dateTo = FormatUtil.formatDate(sqlDateTo);
        // Players that joined within a week of a certain date
        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(*) AS count FROM (SELECT DISTINCT uuid FROM players WHERE join_date BETWEEN ? AND ?) AS a;", (statement) -> {
                    statement.setDate(1, sqlDate);
                    statement.setDate(2, sqlDateTo);
                }))
                .compile()
                .run((table) -> {
                    String count;
                    if (table.isEmpty()) {
                        count = "None";
                    } else {
                        count = FormatUtil.formatNumber(table.getResult().getInt("count"));
                    }
                    
                    embed.addField(String.format("Players that have joined within %s and %s.", dateFrom, dateTo), count, false);
                });
        
        
        Map<Integer, Integer> ranks = new LinkedHashMap<>();
        ranks.put(1, 0);
        ranks.put(2, 0);
        ranks.put(3, 0);
        ranks.put(4, 0);
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT donor, COUNT(*) AS count FROM ranks WHERE donor != 0 AND uuid IN (SELECT DISTINCT uuid FROM players " +
                        "WHERE join_date BETWEEN ? AND ?) GROUP BY donor;", (statement) -> {
                    statement.setDate(1, sqlDate);
                    statement.setDate(2, sqlDateTo);
                }))
                .compile()
                .run((table) -> {
                    for (ResultSet set : table) {
                        ranks.put(set.getInt("donor"), set.getInt("count"));
                    }
                });
        
        embed.addField(String.format("Players that have joined within %s and %s that have donor ranks.", dateFrom, dateTo), String.join("\n", new String[]{
                format(Rank.OVERLORD) + ranks.get(4),
                format(Rank.MYTHIC) + ranks.get(3),
                format(Rank.EMPEROR) + " " + ranks.get(2),
                format(Rank.NOBLE) + ranks.get(1)
        }), false);
        
        
        int betweenDays = event.getArgument("daysrejoin") == null ? days : event.getArgument("daysrejoin");
        
        Date between1;
        if (event.getArgument("daterejoin") == null) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            between1 = c.getTime();
        } else {
            between1 = event.getArgument("daterejoin");
            c.setTime(between1);
        }
        
        c.add(Calendar.DAY_OF_MONTH, betweenDays);
        Date between2 = c.getTime();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(*) AS count FROM (SELECT DISTINCT uuid FROM players " +
                        "WHERE join_date BETWEEN ? AND ?) AS a WHERE uuid IN " +
                        "(SELECT DISTINCT uuid FROM player_join_log WHERE time BETWEEN ? AND ?);", (statement) -> {
                    statement.setDate(1, sqlDate);
                    statement.setDate(2, sqlDateTo);
                    statement.setDate(3, DateUtil.toSqlDate(between1));
                    statement.setDate(4, DateUtil.toSqlDate(between2));
                }))
                .compile()
                .run((table) -> {
                    String count;
                    if (table.isEmpty()) {
                        count = "None";
                    } else {
                        count = FormatUtil.formatNumber(table.getResult().getInt("count"));
                    }
                    
                    embed.addField(String.format("Players that joined again between %s and %s", FormatUtil.formatDate(between1), FormatUtil.formatDate(between2)), count, false);
                });
        
        event.reply(builder);
    }
    
    private String format(Rank rank) {
        return rank.getRankEmote().getFormatted() + " ";
    }
}
