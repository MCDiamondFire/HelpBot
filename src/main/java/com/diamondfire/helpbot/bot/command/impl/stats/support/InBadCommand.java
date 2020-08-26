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
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.List;
import java.util.*;

public class InBadCommand extends Command {

    @Override
    public String getName() {
        return "bad";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets current staff members who have not done a certain number of sessions in 30 days.")
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
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("count",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(1, Integer.MAX_VALUE)).optional(5))
                .addArgument("days",
                        new SingleArgumentContainer<>(new ClampedIntegerArgument(1, Integer.MAX_VALUE)).optional(30));
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
                        new InformativeReply(InformativeReplyType.INFO, String.format("People who have done %s or less sessions in the last %s days:", num, days), null)
                );
        EmbedBuilder embed = preset.getEmbed();
        embed.setColor(Color.RED);

        new SingleQueryBuilder()
                .query("SELECT * FROM (SELECT players.name FROM hypercube.ranks, hypercube.players WHERE ranks.uuid = players.uuid AND ranks.support >= 1 AND ranks.moderation = 0 AND (ranks.developer != 1 || ranks.developer IS NULL)) a " +
                                "WHERE name NOT IN (SELECT DISTINCT staff AS name FROM hypercube.support_sessions WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY GROUP BY staff HAVING COUNT(staff) >= ?)",
                        statement -> {
                            statement.setInt(1, days);
                            statement.setInt(2, num);
                        })
                .onQuery((resultTable) -> {
                    List<String> staff = new ArrayList<>();
                    do {
                        staff.add(StringUtil.display(resultTable.getString("name")));
                    } while (resultTable.next());

                    Util.addFields(embed, staff, "", "", true);
                })
                .onNotFound(() -> embed.setDescription("")).execute();
        event.reply(preset);
    }
}
