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
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Top Support Members in %s days", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        int days = event.getArgument("days");

        new SingleQueryBuilder()
                .query("SELECT DISTINCT staff, COUNT(*) as sessions FROM support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff ORDER BY sessions DESC LIMIT 10", (statement) -> {
                    statement.setInt(1, days);
                })
                .onQuery((resultTable) -> {
                    LinkedHashMap<String, Integer> stats = new LinkedHashMap<>();
                    do {
                        stats.put(StringUtil.display(resultTable.getString("staff")), resultTable.getInt("sessions"));
                    } while (resultTable.next());

                    for (Map.Entry<String, Integer> stat : stats.entrySet()) {
                        embed.addField(stat.getKey(), "\nSessions: " + stat.getValue(), false);
                    }

                }).execute();

        event.reply(preset);
    }

}


