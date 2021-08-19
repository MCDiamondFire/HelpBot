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
public interface Argument<T> {
    
    default T parsed(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return parseValue(args, event);
    }
    
    T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException;
    
    
}

