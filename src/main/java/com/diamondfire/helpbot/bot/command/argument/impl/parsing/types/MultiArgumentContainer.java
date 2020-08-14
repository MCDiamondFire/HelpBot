package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

public class MultiArgumentContainer<T> extends ArgumentContainer<T> {

    public MultiArgumentContainer(Argument<T> argument) {
        super(argument);
    }

    @Override
    public ArgumentParser getParser() {
        return new MultiArgumentParser();
    }
}
