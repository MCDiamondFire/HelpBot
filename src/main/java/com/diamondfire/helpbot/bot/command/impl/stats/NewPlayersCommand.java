package com.diamondfire.helpbot.bot.command.impl.stats;

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

import java.sql.ResultSet;

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
        new DatabaseQuery()
                .query(new BasicQuery("SELECT players.name, approved_users.time FROM approved_users " +
                        "LEFT JOIN players ON approved_users.uuid = players.uuid " +
                        "WHERE time > CURRENT_TIMESTAMP() - INTERVAL 1 DAY ORDER BY time DESC LIMIT 20"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        embed.addField(StringUtil.display(set.getString("name")), set.getTimestamp("time").toString(), false);
                    }
                });

        event.reply(preset);
    }

}


