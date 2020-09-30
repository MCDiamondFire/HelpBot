package com.diamondfire.helpbot.bot.command.impl.stats.support;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.MinecraftUserPreset;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.util.*;

import java.sql.ResultSet;
import java.util.Date;

public class ExcuseStaffCommand extends Command {

    @Override
    public String getName() {
        return "excuse";
    }

    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .description("Excuses a staff member for a certain number of days. This staff member will not appear in joinbad or bad until their duration is up.")
                .category(CommandCategory.SUPPORT)
                .addArgument(
                        new HelpContextArgument()
                                .name("username"),
                        new HelpContextArgument()
                                .name("duration"),
                        new HelpContextArgument()
                                .name("reason")
                                .optional()
                );
    }

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("username",
                        new StringArgument())
                .addArgument("duration",
                        new DateOffsetArgument())
                .addArgument("reason",
                        new SingleArgumentContainer<>(new MessageArgument())
                                .optional("Not Specified")
                );
    }

    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void run(CommandEvent event) {
        String username = event.getArgument("username");
        Date date = event.getArgument("duration");
        String reason = event.getArgument("reason");

        PresetBuilder builder = new PresetBuilder();

        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM hypercube.players WHERE name = ? OR uuid = ?",
                        (statement) -> {
                            statement.setString(1, username);
                            statement.setString(2, username);
                        }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        builder.withPreset(
                                new InformativeReply(InformativeReplyType.ERROR, "Player was not found!")
                        );
                    } else {
                        ResultSet set = result.getResult();
                        String uuid = set.getString("uuid");
                        String name = set.getString("name");

                        new DatabaseQuery()
                                .query(new BasicQuery("INSERT INTO owen.excused_staff (uuid,excused_by,excused_at,excused_till,reason) VALUES (?,?,CURRENT_TIMESTAMP(),?,?)", (statement) -> {
                                    statement.setString(1, uuid);
                                    statement.setString(2, event.getAuthor().getId());
                                    statement.setTimestamp(3, DateUtil.toTimeStamp(date));
                                    statement.setString(4, reason);

                                }))
                                .compile();

                        builder.withPreset(
                                new InformativeReply(InformativeReplyType.SUCCESS, "Excused!", String.format("Staff member will be excused until ``%s``.", FormatUtil.formatDate(date))),
                                new MinecraftUserPreset(name, uuid)
                        );
                    }

                    event.reply(builder);
                });

    }
}
