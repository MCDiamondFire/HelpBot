package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;


//Command exists for easy mobile copy and pasting
public class UuidCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "uuid";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext();
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    protected void execute(CommandEvent event, String player) {
        new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM players WHERE players.name = ? OR players.uuid = ? LIMIT 1;", (statement) -> {
                    statement.setString(1, player);
                    statement.setString(2, player);
                }))
                .compile()
                .run((result) -> {
                    if (result.isEmpty()) {
                        PresetBuilder preset = new PresetBuilder();
                        preset.withPreset(new InformativeReply(InformativeReplyType.ERROR, "Player was not found."));
                        event.reply(preset);
                        return;
                    }
                    
                    event.getReplyHandler().reply(result.getResult().getString("uuid"));
                });
    }
    
}


