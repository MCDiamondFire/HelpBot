package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

import java.util.List;

public class AlternateArgumentContainer<T> extends ArgumentContainer<T, AlternateArgumentParser<T>> {
    private final ArgumentContainer<?,?>[] argumentContainers;

    public AlternateArgumentContainer(ArgumentContainer<?,?>... argumentContainers) {
        this.argumentContainers = argumentContainers;
    }

    @Override
    public AlternateArgumentParser<T> getParser() {
        return new AlternateArgumentParser<>(this);
    }

    public ArgumentContainer<?,?>[] getAlternatives() {
        return argumentContainers;
    }

}
