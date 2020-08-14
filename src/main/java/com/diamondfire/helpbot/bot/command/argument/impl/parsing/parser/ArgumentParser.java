package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.ArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

import java.util.*;

public interface ArgumentParser {

    static ParsedArgumentSet parseArgs(ArgumentSet set, String[] args) throws IllegalArgumentException {
        Map<String, ParsedArgument<?>> parsedArgs = new HashMap<>();
        ArgumentStack stack = new ArgumentStack(set.getArguments(), Arrays.asList(args));
        int arguments = stack.getArguments().size();

        for (int i = 0; i < arguments; i++) {
            ArgumentNode<?> argument = stack.getArguments().pop();
            ArgumentContainer<?> argumentContainer = argument.getContainer();
            String identifier = argument.getIdentifier();

            try {
                parsedArgs.put(identifier, argumentContainer.getParser().parse(argument, stack));
            } catch (NullPointerException exception) {
                Argument<?> arg = argumentContainer.getArgument();
                if (arg.isOptional()) {
                    parsedArgs.put(identifier, new ParsedArgument<>(identifier, arg.getDefaultValue()));
                } else {
                    throw new IllegalArgumentException(String.format("Expected argument at position %s.", i + 1));
                }
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(String.format("Exception while trying to parse argument at position %s: ", i + 1) + exception.getMessage());
            }
        }

        return new ParsedArgumentSet(parsedArgs);
    }

    <T> ParsedArgument<?> parse(ArgumentNode<T> currentNode, ArgumentStack stack) throws IllegalArgumentException;
}
