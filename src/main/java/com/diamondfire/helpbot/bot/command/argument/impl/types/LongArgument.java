package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

public class LongArgument extends AbstractSimpleValueArgument<Long>{
    
    @Override
    protected Long parse(@NotNull String argument) throws ArgumentException {
        return Long.parseLong(argument);
    }
}
