package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public abstract class FallbackArgument<T> extends Argument<T> {
    
    @Override
    public T parsed(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        T result;
    
        try {
            result = super.parsed(args, event);
            args.pop();
        
        } catch (Exception e) {
            result = null;
        }
    
        if (result == null) {
            return ifFail(event);
        
        } else {
            return result;
        }
    }
    
    
    /**
     * The value to return if the parsed value throws an exception or is null.
     * @param event The Command event.
     * @return The new value.
     */
    public abstract T ifFail(CommandEvent event);
    
}
