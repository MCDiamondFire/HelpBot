package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;


// ArgumentContainers contain an argument and a parser.
// Arguments can parse given values using the ArgumentParser.
public abstract class ArgumentContainer<T> {

    private final Argument<T> argument;

    public ArgumentContainer(Argument<T> argument) {
        this.argument = argument;
    }

    public Argument<T> getArgument() {
        return argument;
    }

    public abstract ArgumentParser getParser();
}
