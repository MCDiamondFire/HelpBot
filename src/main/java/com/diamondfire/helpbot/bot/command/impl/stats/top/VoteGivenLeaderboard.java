package com.diamondfire.helpbot.bot.command.impl.stats.top;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.*;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
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
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Player self = DFPlayerArgument.fetchLinkedPlayer(event.getAuthor().getIdLong());
        Leaderboard board = new Leaderboard();
        
        new DatabaseQuery()
                .query(new BasicQuery("SELECT v.uuid, players.name, v.given " +
                        "FROM (SELECT uuid, COUNT(*) AS given FROM plot_votes GROUP BY uuid ORDER BY given DESC LIMIT 10) v " +
                        "LEFT JOIN players ON players.uuid = v.uuid ORDER BY v.given DESC, v.uuid"))
                .compile()
                .run((result) -> {
                    int rank = 1;
                    for (ResultSet set : result) {
                        board.add(rank++, set.getString("name"), votes(set.getInt("given")),
                                self != null && self.uuidString().equals(set.getString("uuid")));
                    }
                });
        
        if (self != null) {
            int[] given = new int[1];
            new DatabaseQuery()
                    .query(new BasicQuery("SELECT COUNT(*) AS given FROM plot_votes WHERE uuid = ?", (statement) -> statement.setString(1, self.uuidString())))
                    .compile()
                    .run((result) -> given[0] = result.getResult().getInt("given"));
            
            if (given[0] > 0) {
                new DatabaseQuery()
                        .query(new BasicQuery("SELECT COUNT(*) + 1 AS position FROM (SELECT uuid FROM plot_votes GROUP BY uuid HAVING COUNT(*) > ?) higher",
                                (statement) -> statement.setInt(1, given[0])))
                        .compile()
                        .run((result) -> board.self(result.getResult().getLong("position"), self.name(), votes(given[0])));
            }
        }
        
        event.reply(new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Votes Given Leaderboard", board.build())
                ));
    }
    
    private static String votes(int votes) {
        return FormatUtil.formatNumber(votes) + " " + StringUtil.sCheck("vote", votes);
    }
    
}
