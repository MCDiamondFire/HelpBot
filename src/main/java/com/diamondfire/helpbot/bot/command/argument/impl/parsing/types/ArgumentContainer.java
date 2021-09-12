package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.function.Function;


/**
 * Argument containers provide essential information for the argument.
 * These give a parser.
 * @param <T>
 */
public abstract class ArgumentContainer<T> {
    
    private Function<CommandEvent, T> defaultValueFunction;
    private boolean isOptional;
    
    public abstract <P extends ArgumentParser<?, T>> P getParser();
    
    public ArgumentContainer<T> optional(Function<CommandEvent, T> defaultValueFunction) {
        this.defaultValueFunction = defaultValueFunction;
        this.isOptional = true;
        return this;
    }
    
    public boolean isOptional() {
        return isOptional;
    }
    
    public Function<CommandEvent, T> getDefaultValueFunction() {
        return defaultValueFunction;
    }
    
    public T getDefaultValue(CommandEvent event) {
        return getDefaultValueFunction().apply(event);
    }
    
}
