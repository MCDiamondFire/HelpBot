package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

public abstract class SingleContainer<T, P extends ArgumentParser<?, T>> extends ArgumentContainer<T, P> {

    private final Argument<T> argument;

    public SingleContainer(Argument<T> argument) {
        this.argument = argument;
    }

    public Argument<T> getArgument() {
        return argument;
    }
}
