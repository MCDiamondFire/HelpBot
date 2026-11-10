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

public class TokenTopCommand extends Command {
    
    @Override
    public String getName() {
        return "tokentop";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"credittop", "creditleaderboard"};
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Gets the current token leaderboard.")
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
                .query(new BasicQuery("SELECT players.uuid, name, tokens FROM players, user_tokens WHERE players.uuid = user_tokens.uuid ORDER BY tokens DESC LIMIT 10"))
                .compile()
                .run((result) -> {
                    int rank = 1;
                    for (ResultSet set : result) {
                        board.add(rank++, set.getString("name"), tokens(set.getInt("tokens")),
                                self != null && self.uuidString().equals(set.getString("uuid")));
                    }
                });
        
        if (self != null) {
            new DatabaseQuery()
                    .query(new BasicQuery("SELECT me.tokens, (SELECT COUNT(*) FROM user_tokens other, players WHERE players.uuid = other.uuid AND other.tokens > me.tokens) + 1 AS position " +
                            "FROM user_tokens me WHERE me.uuid = ?", (statement) -> statement.setString(1, self.uuidString())))
                    .compile()
                    .run((result) -> {
                        if (!result.isEmpty()) {
                            ResultSet set = result.getResult();
                            board.self(set.getLong("position"), self.name(), tokens(set.getInt("tokens")));
                        }
                    });
        }
        
        event.reply(new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, "Token Leaderboard", board.build())
                ));
    }
    
    private static String tokens(int tokens) {
        return FormatUtil.formatNumber(tokens) + " " + StringUtil.sCheck("token", tokens);
    }
    
}
