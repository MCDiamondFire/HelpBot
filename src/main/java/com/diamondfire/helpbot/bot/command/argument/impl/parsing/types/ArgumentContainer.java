package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;


// ArgumentContainers contain an argument and a parser.
// Arguments can parse given values using the ArgumentParser.
public abstract class ArgumentContainer<T> {

    private final Argument<T> argument;
    private T defaultValue;
    private boolean isOptional;

    public ArgumentContainer(Argument<T> argument) {
        this.argument = argument;
    }

    public Argument<T> getArgument() {
        return argument;
    }

    public abstract ArgumentParser getParser();

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
