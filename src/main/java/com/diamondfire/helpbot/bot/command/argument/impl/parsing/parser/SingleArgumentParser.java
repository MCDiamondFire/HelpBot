package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.SingleArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

import java.util.Deque;

public class SingleArgumentParser<A> extends ArgumentParser<SingleArgumentContainer<A>, A> {
    
    
    public SingleArgumentParser(SingleArgumentContainer<A> container) {
        super(container);
    }
    
    @Override
    public ParsedArgument<?> parse(String identifier, ArgumentStack.RawArgumentStack args) throws ArgumentException {
        Deque<String> rawArgs = args.popStack();
        Argument<A> arg = getContainer().getArgument();
        
        if (rawArgs.peek() == null) {
            throw new MissingArgumentException("Expected an argument, but got nothing.");
        }
        
        return new ParsedArgument<>(identifier, arg.parseValue(rawArgs));
    }
}
