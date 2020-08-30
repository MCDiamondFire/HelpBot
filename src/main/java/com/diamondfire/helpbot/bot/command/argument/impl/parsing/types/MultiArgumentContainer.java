package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.MultiArgumentParser;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

public class MultiArgumentContainer<T> extends SingleContainer<T> {

    public MultiArgumentContainer(Argument<T> argument) {
        super(argument);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MultiArgumentParser<T> getParser() {
        return new MultiArgumentParser<>(this);
    }

}
