package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
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
                                .name("days OR all")
                                .optional()
                );
    }
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("days",
                        new AlternateArgumentContainer<>(
                                new SingleArgumentContainer<>(new ClampedIntegerArgument(1)),
                                new SingleArgumentContainer<>(new DefinedObjectArgument<>("all"))
                        ).optional(30)
                );
    }
    
    @Override
    public Permission getPermission() {
        return Permission.SUPPORT;
    }
    
    @Override
    public void run(CommandEvent event) {
        Object arg = event.getArgument("days");
        boolean allTime = arg instanceof String;
        int days = allTime ? 0 : (int) arg;
        
        String title = allTime
                ? "Top Support Members, All Time"
                : String.format("Top Support Members, Past %s %s", days, StringUtil.sCheck("Day", days));
        String where = allTime ? "" : "WHERE time > CURRENT_TIMESTAMP - INTERVAL ? DAY ";
        
        Leaderboard board = new Leaderboard();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT staff, COUNT(*) AS total FROM support_sessions " + where + "GROUP BY staff ORDER BY total DESC LIMIT 10", (statement) -> {
                    if (!allTime) {
                        statement.setInt(1, days);
                    }
                }))
                .compile()
                .run((result) -> {
                    int rank = 1;
                    for (ResultSet set : result) {
                        board.add(rank++, set.getString("staff"), FormatUtil.formatNumber(set.getInt("total")) + " " + StringUtil.sCheck("session", set.getInt("total")), false);
                    }
                });
        
        event.reply(new PresetBuilder()
                .withPreset(
                        new InformativeReply(InformativeReplyType.INFO, title, board.isEmpty() ? "No support sessions in this period." : board.build())
                ));
    }
    
}
