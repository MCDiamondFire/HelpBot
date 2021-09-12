package com.diamondfire.helpbot.bot.command.argument.impl.types.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

public class IntegerArgument extends AbstractSimpleValueArgument<Integer> {
    
    @Override
    public Integer parse(@NotNull String msg, CommandEvent event) throws ArgumentException {
        try {
            return Integer.parseInt(msg);
        } catch (NumberFormatException exception) {
            throw new MalformedArgumentException("Invalid number provided");
        }
    }
}
