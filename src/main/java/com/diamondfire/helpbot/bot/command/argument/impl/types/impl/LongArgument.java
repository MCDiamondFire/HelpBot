package com.diamondfire.helpbot.bot.command.argument.impl.types.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

public class LongArgument extends AbstractSimpleValueArgument<Long> {
    
    @Override
    protected Long parse(@NotNull String argument, CommandEvent event) throws ArgumentException {
        return Long.parseLong(argument);
    }
}
