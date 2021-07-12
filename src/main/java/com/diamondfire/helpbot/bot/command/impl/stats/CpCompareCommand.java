package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.help.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.database.impl.DatabaseQuery;
import com.diamondfire.helpbot.sys.database.impl.queries.BasicQuery;
import com.diamondfire.helpbot.sys.database.impl.result.DatabaseResult;

import java.util.UUID;

public class CpCompareCommand extends Command {
    
    @Override
    public String getName() {
        return "cpcompare";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext().addArgument(new HelpContextArgument().name("Player to compare"),
                new HelpContextArgument().name("Comparing player")).category(CommandCategory.PLAYER_STATS).description("Compare the creator points of two players");
    }
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument("player",new MinecraftPlayerUUIDArgument())
                .addArgument("player1",new MinecraftPlayerUUIDArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        UUID player = event.getArgument("player");
        UUID player1 = event.getArgument("player1");
        
        long playerCP;
        long player1CP;
    
       DatabaseResult res = new DatabaseQuery()
                .query(new BasicQuery("SELECT * FROM owen.creator_rankings_log WHERE uuid = ? ORDER BY points DESC LIMIT 1",(statement) -> statement.setString(1,player.toString())))
                .compile().get();
       res.getResult().
       
    }
}
