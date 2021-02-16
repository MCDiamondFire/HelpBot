package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;

import java.sql.ResultSet;
import java.util.*;

public class SessionsCommand extends AbstractSessionLogCommand {
    
    @Override
    public String getName() {
        return "sessions";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a file of all the sessions a support member has completed.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }
    
    @Override
    protected List<AbstractSessionLogCommand.Session> getSessions(String player) {
        List<Session> sessions = new ArrayList<>();
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM support_sessions WHERE staff = ? ORDER BY time", (statement) -> statement.setString(1, player)))
                .compile()
                .run((result) -> {
                    for (ResultSet set : result) {
                        sessions.add(new Session(set.getString("name"), set.getLong("duration"), set.getTimestamp("time")));
                    }
                });
        return sessions;
    }
}
