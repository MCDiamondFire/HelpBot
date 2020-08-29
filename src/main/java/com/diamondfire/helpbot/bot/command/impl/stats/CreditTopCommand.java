package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

public class CreditTopCommand extends Command {

    @Override
    public String getName() {
        return "credittop";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"creditleaderboard"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current credit leaderboard.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Credit Leaderboard", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        new SingleQueryBuilder()
                .query("SELECT name, credits FROM players,player_credits WHERE players.uuid = player_credits.uuid ORDER BY credits DESC LIMIT 10")
                .onQuery((resultTable) -> {
                    do {
                        embed.addField(StringUtil.display(resultTable.getString("name")),
                                "Credits: " + FormatUtil.formatNumber(resultTable.getInt("credits")), false);
                    } while (resultTable.next());
                }).execute();

        event.reply(preset);
    }

}


