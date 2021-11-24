package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import org.jetbrains.annotations.NotNull;

public class LongArgument extends AbstractSimpleValueArgument<Long> {
    
    @Override
    public Long parse(@NotNull String argument, CommandEvent event) throws ArgumentException {
        return Long.parseLong(argument);
    }
    
    // Leave OptionType as STRING due to discord only having 53 bit precision on longs.
}
