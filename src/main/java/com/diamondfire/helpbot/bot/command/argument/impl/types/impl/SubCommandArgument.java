package com.diamondfire.helpbot.bot.command.argument.impl.types.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SubCommandArgument extends AbstractSimpleValueArgument<SubCommand> {
    
    @Override
    public SubCommand parse(@NotNull String msg, CommandEvent event) throws ArgumentException {
        try {
            return Arrays.stream(((SubCommandHolder) event.getCommand()).getSubCommands())
                    .filter(s -> s.getName().equals(msg)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedArgumentException(
                    "Not a valid subcommand. Choose from " + event.getCommand().getHelpContext().getArguments().get(0).getArgumentName());
        }
    }
    
}
