package com.diamondfire.helpbot.bot.command.impl.other.mod.channelmodifier;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

// TODO: Finish, pushing so T can have it on another computer. - tk
public abstract class AbstractChannelModificationCommand extends Command {
    
    @Override
    protected ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("state", new DefinedObjectArgument<>())
                .addArgument("channel", DiscordMentionArgument.channel())
                .addArgument("reason", new GreedyStringArgument());
    }
    
    @Override
    public Permission getPermission() {
        return Permission.MODERATION;
    }
    
    @Override
    public void run(CommandEvent event) {
    
    }
}
