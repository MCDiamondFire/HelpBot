package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class NewPlayersCommand extends Command {

    @Override
    public String getName() {
        return "newplayers";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the first 25 new players who have joined in the last 24 hours.")
                .category(CommandCategory.STATS);
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet();
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Players who have joined in last 24 hours:", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new SingleQueryBuilder()
                .query("SELECT players.name, approved_users.time FROM approved_users " +
                        "LEFT JOIN players ON approved_users.uuid = players.uuid " +
                        "WHERE time > CURRENT_TIMESTAMP() - INTERVAL 1 DAY ORDER BY time DESC LIMIT 20")
                .onQuery((resultTable) -> {
                    do {
                        embed.addField(StringUtil.display(resultTable.getString("name")), resultTable.getTimestamp("time").toString(), false);
                    } while (resultTable.next());
                }).execute();

        event.reply(preset);
    }

}


