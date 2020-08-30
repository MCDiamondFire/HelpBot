package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;


// Argument containers provide essential information for the argument.
// These give a parser.
public abstract class ArgumentContainer<T> {

    private T defaultValue;
    private boolean isOptional;

    public abstract <P extends ArgumentParser<?, T>> P getParser();

    public ArgumentContainer<T> optional(T defaultValue) {
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
