package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

/**
 * Arguments simply parse a given value and can remove it from the stack if they process it correctly.
 * This means an argument can actually use more than one argument if they want!
 *
 * @param <T> parsed value type
 */
public abstract class Argument<T> {
    
    public T parsed(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return parseValue(args, event);
    }
    
    protected abstract T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException;
    
    
}

