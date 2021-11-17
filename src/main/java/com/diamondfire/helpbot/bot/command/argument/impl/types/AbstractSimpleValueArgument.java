package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public abstract class AbstractSimpleValueArgument<T> implements Argument<T> {
    
    @Override
    public T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return parse(args.pop(), event);
    }
    
    public abstract T parse(@NotNull String argument, CommandEvent event) throws ArgumentException;
    
}
