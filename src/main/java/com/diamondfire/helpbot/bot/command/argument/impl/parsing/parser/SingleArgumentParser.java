package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

import java.util.Deque;

public class SingleArgumentParser implements ArgumentParser {

    @Override
    public <T> ParsedArgument<T> parse(ArgumentNode<T> currentNode, ArgumentStack stack) throws IllegalArgumentException {
        Deque<String> args = stack.getRawArguments();
        Argument<T> arg = currentNode.getContainer().getArgument();
        ParsedArgument<T> parsedArgument;

        String rawArg = args.peek();
        if (rawArg == null) {
            throw new NullPointerException("Expected an argument, but got nothing.");
        }
        parsedArgument = new ParsedArgument<>(currentNode.getIdentifier(), arg.parseValue(rawArg));
        args.pop();

        return parsedArgument;
    }
}
