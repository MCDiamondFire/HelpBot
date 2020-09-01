package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;

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
        new SingleQueryBuilder()
                .query("SELECT * FROM hypercube.support_sessions WHERE staff = ? ORDER BY time", (statement) -> statement.setString(1, player))
                .onQuery((table) -> {
                    do {
                        sessions.add(new Session(table.getString("name"), table.getLong("duration"), table.getTimestamp("time")));
                    } while (table.next());
                }).execute();
        return sessions;
    }
}
