package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public abstract class AbstractSimpleValueFallbackArgument<T> implements FallbackArgument<T> {
    
    @Override
    @SuppressWarnings("all")
    public T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return parse(args.peek(), event);
    }
    
    protected abstract T parse(@NotNull String argument, CommandEvent event) throws ArgumentException;
    
}
