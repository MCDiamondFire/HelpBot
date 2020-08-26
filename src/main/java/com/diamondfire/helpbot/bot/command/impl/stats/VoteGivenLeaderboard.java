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


public class VoteGivenLeaderboard extends Command {

    @Override
    public String getName() {
        return "votetop";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"vgiventop", "votesgiventop"};
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets top 10 players who have given out the most votes.")
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
                        new InformativeReply(InformativeReplyType.INFO, "Votes Given Leaderboard", null)
                );
        EmbedBuilder embed = preset.getEmbed();

        new SingleQueryBuilder()
                .query("SELECT COUNT(plot_votes.uuid) AS given, name FROM plot_votes, players WHERE players.uuid = plot_votes.uuid GROUP BY plot_votes.uuid ORDER BY COUNT(plot_votes.uuid) DESC LIMIT 10")
                .onQuery((resultTable) -> {
                    do {
                        embed.addField(StringUtil.display(resultTable.getString("name")),
                                "Votes Given: " + StringUtil.formatNumber(resultTable.getInt("given")), false);
                    } while (resultTable.next());
                }).execute();
        event.reply(preset);
    }

}
