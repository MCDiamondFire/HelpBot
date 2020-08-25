package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.StringArgument;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public abstract class AbstractPlayerUUIDCommand extends Command {

    @Override
    public ArgumentSet getArguments() {
        return new ArgumentSet()
                .addArgument("player",
                        new SingleArgumentContainer<>(new StringArgument()).optional(null));
    }

    @Override
    public Permission getPermission() {
        return Permission.USER;
    }

    @Override
    public void run(CommandEvent event) {
        String player = event.getArgument("player");
        if (player == null) {
            execute(event, event.getMember().getEffectiveName());
        } else {
            execute(event, player);
        }
    }

    protected abstract void execute(CommandEvent event, String player);

}


