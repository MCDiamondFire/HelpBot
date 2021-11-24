package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;

public class CommandLog implements CommandCheck {
    
    @Override
    public boolean check(CommandEvent event) {
        if (!HelpBotInstance.getConfig().isDevBot()) {
            new Thread(() -> {
                new DatabaseQuery()
                        .query(new BasicQuery("INSERT INTO owen.cmd_log (user, command, alias, channel, time) VALUES (?,?,?,?,CURRENT_TIMESTAMP())", (statement) -> {
                            statement.setLong(1, event.getMember().getIdLong());
                            statement.setString(2, event.getCommand().getName());
                            statement.setString(3, event.getAliasUsed());
                            statement.setLong(4, event.getChannel().getIdLong());
                        })).compile();
                
            }).start();
        }
        return true;
    }
    
    @Override
    public void buildMessage(CommandEvent event, PresetBuilder builder) {
    }
    
    
}
