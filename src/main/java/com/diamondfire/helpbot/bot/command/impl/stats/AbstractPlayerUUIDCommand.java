package com.diamondfire.helpbot.bot.command.impl.stats;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.impl.minecraft.*;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Rank;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public abstract class AbstractPlayerUUIDCommand extends Command {
    
    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet()
                .addArgument("player",
                        new SingleArgumentContainer<>(new DFPlayerArgument()).optional(null));
    }
    
    @Override
    public Rank getRank() {
        return Rank.USER;
    }
    
    @Override
    public void run(CommandEvent event) {
        Player player = event.getArgument("player");
        if (player == null) {
            execute(event, DFPlayerArgument.fetchPlayer(event.getMember().getEffectiveName()));
        } else {
            execute(event, player);
        }
    }
    
    protected abstract void execute(CommandEvent event, Player player);
    
}


