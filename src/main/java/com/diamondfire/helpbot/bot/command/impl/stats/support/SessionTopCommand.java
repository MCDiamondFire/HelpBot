package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.ClampedIntegerArgument;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class SessionTopCommand extends Command {

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"sessiontop"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets support members with the top sessions in a certain number of days.")
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
                        new InformativeReply(InformativeReplyType.INFO, String.format("Top Support Members in %s days", days), null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT DISTINCT staff, COUNT(*) as sessions FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff ORDER BY sessions DESC LIMIT 10", (statement) -> {
                    statement.setInt(1, days);
                })
                .onQuery((resultTable) -> {
                    do {
                        embed.addField(StringUtil.display(resultTable.getString("staff")),
                                "\nSessions: " + StringUtil.formatNumber(resultTable.getInt("sessions")), false);
                    } while (resultTable.next());

                }).execute();

        event.reply(preset);
    }

}


