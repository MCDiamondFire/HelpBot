package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;

public class AlternateArgumentParser<A> extends ArgumentParser<AlternateArgumentContainer<A>, A> {

    public AlternateArgumentParser(AlternateArgumentContainer<A> container) {
        super(container);
    }

    @Override
    public ParsedArgument<?> parse(String identifier, ArgumentStack stack) throws ArgumentException {
        for (ArgumentContainer<?,?> container : getContainer().getAlternatives()) {
            try {
                return new ParsedArgument<>(identifier, container.getParser().parse(identifier, stack).getValue());
            } catch (ArgumentException ignored) {}
        }

        throw new MissingArgumentException("No valid arguments were provided.");
    }
}
