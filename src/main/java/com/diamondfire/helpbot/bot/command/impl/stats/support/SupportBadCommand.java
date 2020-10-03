package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.ResultSet;
import java.util.List;
import java.util.*;

public class SupportBadCommand extends Command {

    @Override
    public String getName() {
        return "bad";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current staff members who have not done a certain number of sessions in certain number of days.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("sessions")
                                .optional(),
                        new HelpContextArgument()
                                .name("days")
                                .optional()
                );
    }

    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("count",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(0)).optional(5))
                .addArgument("days",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(0)).optional(30));
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    public void run(CommandEvent event) {
        int num = event.getArgument("count");
        int days = event.getArgument("days");

        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("Staff who have done %s or less sessions in the last %s %s", num, days, StringUtil.sCheck("day", days)), null)
                );

        EmbedBuilder embed = preset.getEmbed();
        embed.setColor(Color.RED);
        embed.setDescription("");

        new DatabaseQuery()
                .query(new BasicQuery("SELECT DISTINCT p.name, count " +
                        "FROM (SELECT players.name" +
                        "      FROM hypercube.ranks," +
                        "           hypercube.players" +
                        "      WHERE ranks.uuid = players.uuid" +
                        "        AND ranks.support >= 1" +
                        "        AND ranks.moderation = 0" +
                        "        AND (ranks.developer != 1 || ranks.developer IS NULL)" +
                        "        AND players.uuid NOT IN (SELECT DISTINCT uuid FROM owen.excused_staff WHERE excused_till > CURRENT_TIMESTAMP())) p" +
                        "         LEFT OUTER JOIN (SELECT DISTINCT staff AS name, COUNT(staff) AS count" +
                        "                          FROM hypercube.support_sessions" +
                        "                          WHERE time > CURRENT_TIMESTAMP() - INTERVAL ? DAY" +
                        "                          GROUP BY staff) cn ON cn.name = p.name " +
                        "WHERE cn.count <= ?" +
                        "   OR count IS NULL " +
                        "ORDER BY count DESC;", statement -> {
                    statement.setInt(1, days);
                    statement.setInt(2, num);
                }))
                .compile()
                .run((result) -> {
                    List<String> staff = new ArrayList<>();
                    for (ResultSet set : result) {
                        staff.add(StringUtil.display(set.getString("name") + " (" + FormatUtil.formatNumber(set.getInt("count"))) + ")");
                    }

                    EmbedUtil.addFields(embed, staff, "", "", true);
                });

        event.reply(preset);
    }
}
