package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

/**
 * Arguments simply parse a given value and can remove it from the stack if they process it correctly.
 * This means an argument can actually use more than one argument if they want!
 *
 * @param <T> parsed value type
 */
public interface Argument<T> {
    
    T parseValue(@NotNull Deque<String> args) throws ArgumentException;
    
}

