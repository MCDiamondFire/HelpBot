package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.SingleQueryBuilder;
import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.util.StringUtil;

import java.io.File;
import java.nio.file.*;
import java.util.*;

public class SupporteeSessionsCommand extends AbstractSessionLogCommand {

    @Override
    public String getName() {
        return "supporteesessions";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Generates a file of all the sessions a supportee has completed.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("player|uuid")
                                .optional()
                );
    }

    @Override
    protected List<Session> getSessions(String player) {
        List<Session> sessions = new ArrayList<>();
        new SingleQueryBuilder()
                .query("SELECT * FROM hypercube.support_sessions WHERE name = ? ORDER BY time", (statement) -> statement.setString(1, player))
                .onQuery((table) -> {
                    do {
                        sessions.add(new Session(table.getString("staff"), table.getLong("duration"), table.getTimestamp("time")));
                    } while (table.next());
                }).execute();
        return sessions;
    }
}
