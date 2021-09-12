package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.events.CommandEvent;


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
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        event.getReplyHandler().reply(player.uuidString());
    }
    
}


