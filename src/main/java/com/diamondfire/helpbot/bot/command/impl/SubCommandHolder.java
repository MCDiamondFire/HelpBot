package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.types.SubCommandArgument;
import com.diamondfire.helpbot.bot.events.commands.*;

import java.util.*;

public abstract class SubCommandHolder extends Command {

    @Override
    public ArgumentSet compileArguments() {
        return new ArgumentSet().addArgument(
                "subcommand", new SubCommandArgument()
        );
    }

    @Override
    public void run(CommandEvent event) {
        SubCommand subcommand = event.getArgument("subcommand");
        event.setCommand(subcommand);
    
        if (event instanceof MessageCommandEvent messageCommandEvent) {
            String[] rawArgs = messageCommandEvent.getRawArgs();
            rawArgs[0] = rawArgs[0].substring(HelpBotInstance.getConfig().getPrefix().length());
    
            List<String> args = new ArrayList<>(Arrays.asList(rawArgs));
            args.remove(1);
    
            CommandHandler.getInstance().run(messageCommandEvent, args.toArray(String[]::new));
        }
    }

    public abstract SubCommand[] getSubCommands();
}
