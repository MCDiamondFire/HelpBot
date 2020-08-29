package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

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
                                .name("days OR all")
                                .optional()
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("days",
                        new AlternateArgumentContainer<>(
                                new SingleArgumentContainer<>(new ClampedIntegerArgument(1)),
                                new SingleArgumentContainer<>(new DefinedObjectArgument<>("all"))
                        ).optional(30));
    }

    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder();
        Object arg = event.getArgument("days");

        if (arg instanceof String) {
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.INFO, "Top Support Member Time of all time", null)
            );
            EmbedBuilder embed = preset.getEmbed();

            new SingleQueryBuilder()
                    .query("SELECT DISTINCT staff, SUM(duration) AS sessions FROM support_sessions GROUP BY staff ORDER BY sessions DESC LIMIT 10")
                    .onQuery((resultTable) -> {
                        do {
                            embed.addField(StringUtil.display(resultTable.getString("staff")),
                                    "\nTotal Duration: " + FormatUtil.formatMilliTime(resultTable.getLong("sessions")), false);
                        } while (resultTable.next());

                    }).execute();
        } else {
            int days = (int) arg;
            preset.withPreset(
                    new InformativeReply(InformativeReplyType.INFO, String.format("Top Support Member Time in %s %s", days, StringUtil.sCheck("day", days)), null)
            );
            EmbedBuilder embed = preset.getEmbed();

            new SingleQueryBuilder()
                    .query("SELECT DISTINCT staff, SUM(duration) AS sessions FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff ORDER BY sessions DESC LIMIT 10", (statement) -> statement.setInt(1, days))
                    .onQuery((resultTable) -> {
                        do {
                            embed.addField(StringUtil.display(resultTable.getString("staff")),
                                    "\nTotal Duration: " + FormatUtil.formatMilliTime(resultTable.getLong("sessions")), false);
                        } while (resultTable.next());

                    }).execute();
        }
        event.reply(preset);
    }

}


