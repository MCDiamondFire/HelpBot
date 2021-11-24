package com.diamondfire.helpbot.bot.command.impl.stats.individualized;

import com.diamondfire.helpbot.bot.command.argument.impl.types.minecraft.Player;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.impl.stats.AbstractPlayerUUIDCommand;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import com.diamondfire.helpbot.bot.command.help.HelpContextArgument;


//Command exists for easy mobile copy and pasting
public class UuidCommand extends AbstractPlayerUUIDCommand {
    
    @Override
    public String getName() {
        return "uuid";
    }
    
    @Override
    public HelpContext getHelpContext() {
        return new HelpContext()
                .addArgument(new HelpContextArgument()
                        .name("player name|uuid")
                        .optional());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.USER;
    }
    
    @Override
    protected void execute(CommandEvent event, Player player) {
        event.getReplyHandler().reply(player.uuidString());
    }
    
}


