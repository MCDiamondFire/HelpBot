package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.SingleArgumentParser;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

public class SingleArgumentContainer<T> extends SingleContainer<T> {

    public SingleArgumentContainer(Argument<T> argument) {
        super(argument);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SingleArgumentParser<T> getParser() {
        return new SingleArgumentParser<>(this);
    }

}
