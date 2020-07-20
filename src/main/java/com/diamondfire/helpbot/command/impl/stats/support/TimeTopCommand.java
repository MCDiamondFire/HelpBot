package com.diamondfire.helpbot.command.impl.stats.support;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.command.help.*;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.components.database.SingleQueryBuilder;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class TimeTopCommand extends Command {

    @Override
    public String getName() {
        return "timetop";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"toptime"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets support members with the top session time in a certain number of days.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("days")
                                .optional()
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("days",
                        new ClampedIntegerArgument(1, 4000000).optional(30));
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    public void run(CommandEvent event) {
        int days = event.getArgument("days");
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, String.format("Top Support Member's time in %s days", days), null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT DISTINCT staff, SUM(duration) AS sessions FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff ORDER BY sessions DESC LIMIT 10", (statement) -> {
                    statement.setInt(1, days);
                })
                .onQuery((resultTable) -> {
                    LinkedHashMap<String, Long> stats = new LinkedHashMap<>();
                    do {
                        stats.put(StringUtil.display(resultTable.getString("staff")), resultTable.getLong("sessions"));
                    } while (resultTable.next());

                    for (Map.Entry<String, Long> stat : stats.entrySet()) {
                        embed.addField(stat.getKey(), "\nTotal Duration: " + StringUtil.formatMilliTime(stat.getValue()), false);
                    }

                }).execute();
        event.reply(preset);
    }

}


