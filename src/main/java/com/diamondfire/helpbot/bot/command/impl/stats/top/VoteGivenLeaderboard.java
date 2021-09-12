package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;


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
                .category(CommandCategory.GENERAL_STATS);
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet();
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        PresetBuilder preset = new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Votes Given Leaderboard", null)
                );
        EmbedBuilder embed = preset.getEmbed();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT COUNT(plot_votes.uuid) AS given, name FROM plot_votes, players WHERE players.uuid = plot_votes.uuid GROUP BY plot_votes.uuid ORDER BY COUNT(plot_votes.uuid) DESC LIMIT 10"))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        embed.addField(StringUtil.display(set.getString("name")),
                                "Votes Given: " + FormatUtil.formatNumber(set.getInt("given")), false);
                    }
                });
        event.reply(preset);
    }
    
}
