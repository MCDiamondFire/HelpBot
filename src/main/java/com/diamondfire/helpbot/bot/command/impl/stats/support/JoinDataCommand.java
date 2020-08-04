package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

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
                ).category(CommandCategory.STATS);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("date",
                        new DateArgument())
                .addArgument("days",
                        new ClampedIntegerArgument(1).optional(7))
                .addArgument("daterejoin",
                        new DateArgument().optional(null))
                .addArgument("daysrejoin",
                        new ClampedIntegerArgument(1).optional(null));

    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();
        EmbedBuilder embed = builder.getEmbed();
        builder.withPreset(
                new InformativeReply(InformativeReplyType.INFO, "Join Statistics")
        );

        int days = event.getArgument("days");
        Date date = event.getArgument("date");
        java.sql.Date sqlDate = new java.sql.Date(date.toInstant().toEpochMilli());

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);

        java.sql.Date sqlDateTo = new java.sql.Date(c.getTime().toInstant().toEpochMilli());

        // convert calendar to date
        String dateFrom = StringUtil.formatDate(date);
        String dateTo = StringUtil.formatDate(c.getTime());

        // Players that joined within a week of a certain date
        new SingleQueryBuilder().query("SELECT COUNT(*) AS count FROM (SELECT DISTINCT uuid FROM approved_users WHERE time BETWEEN ? AND ?) AS a;",
                (statement) -> {
                    statement.setDate(1, sqlDate);
                    statement.setDate(2, sqlDateTo);
                }).onQuery((table) -> embed.addField(String.format("Players that have joined within %s and %s.", dateFrom, dateTo), String.valueOf(table.getInt("count")), false)).onNotFound(() -> embed.addField(String.format("Players that have joined within %s and %s.", dateFrom, dateTo), "None...", false)).execute();


        Map<Integer, Integer> ranks = new LinkedHashMap<>();
        ranks.put(1, 0);
        ranks.put(2, 0);
        ranks.put(3, 0);
        ranks.put(4, 0);

        new SingleQueryBuilder().query("SELECT donor, COUNT(*) AS count FROM ranks WHERE donor != 0 AND uuid IN (SELECT DISTINCT uuid FROM approved_users " +
                        "WHERE time BETWEEN ? AND ?) GROUP BY donor;",
                (statement) -> {
                    statement.setDate(1, sqlDate);
                    statement.setDate(2, sqlDateTo);
                }).onQuery((table) -> {
            do {
                ranks.put(table.getInt("donor"), table.getInt("count"));
            } while (table.next());
        }).execute();

        embed.addField(String.format("Players that have joined within %s and %s that have donor ranks.", dateFrom, dateTo), String.join("\n", new String[]{
                "<:overlord:735940074742612030> Overlord: " + ranks.get(4),
                "<:mythic:735940074662789130> Mythic: " + ranks.get(3),
                "<:emperor:735940074595680366> Emperor: " + ranks.get(2),
                "<:noble:735940074285432834> Noble: " + ranks.get(1)
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

        new SingleQueryBuilder().query("SELECT COUNT(*) AS count FROM (SELECT DISTINCT uuid FROM approved_users " +
                        "WHERE time BETWEEN ? AND ? + INTERVAL ? DAY) AS a WHERE uuid IN " +
                        "(SELECT DISTINCT uuid FROM player_join_log WHERE time BETWEEN ? AND ?);",
                (statement) -> {
                    statement.setDate(1, sqlDate);
                    statement.setDate(2, sqlDate);
                    statement.setInt(3, days);
                    statement.setDate(4, new java.sql.Date(between1.toInstant().toEpochMilli()));
                    statement.setDate(5, new java.sql.Date(between2.toInstant().toEpochMilli()));
                }).onQuery((table) -> embed.addField(String.format("Players that joined again between %s and %s", StringUtil.formatDate(between1), StringUtil.formatDate(between2)), String.valueOf(table.getInt("count")), false)).onNotFound(() -> embed.addField(String.format("Players that joined again between between %s and %s", StringUtil.formatDate(between1), StringUtil.formatDate(between2)), "None...", false)).execute();

        event.reply(builder);
    }
}
