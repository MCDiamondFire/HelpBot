package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;


// Argument containers provide essential information for the argument.
// These give a parser.
public abstract class ArgumentContainer<T, P extends ArgumentParser<?, T>> {

    private T defaultValue;
    private boolean isOptional;

    public abstract P getParser();

    public ArgumentContainer<T, P> optional(T defaultValue) {
        this.defaultValue = defaultValue;
        this.isOptional = true;
        return this;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
