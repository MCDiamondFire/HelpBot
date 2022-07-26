package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;

public class AlternateArgumentParser<A> extends ArgumentParser<AlternateArgumentContainer<A>, A> {
    
    public AlternateArgumentParser(AlternateArgumentContainer<A> container) {
        super(container);
    }
    
    @Override
    public ParsedArgument<?> parse(String identifier, ArgumentStack.RawArgumentStack args, CommandEvent event) throws ArgumentException {
        for (ArgumentContainer<?> container : getContainer().getAlternatives()) {
            try {
                return new ParsedArgument<>(identifier, container.getParser().parse(identifier, args, event).getValue());
            } catch (ArgumentException ignored) {
            }
        }
        
        throw MissingArgumentException.noValidArguments();
    }
}
