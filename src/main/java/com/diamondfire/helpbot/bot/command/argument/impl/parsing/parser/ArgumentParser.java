package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.ArgumentContainer;
import com.diamondfire.helpbot.bot.command.impl.Command;

import java.util.*;

public interface ArgumentParser {

    static ParsedArgumentSet parseArgs(Command command, String[] args) throws ArgumentException {
        Map<String, ParsedArgument<?>> parsedArgs = new HashMap<>();
        ArgumentStack stack = new ArgumentStack(command.getArguments().getArguments(), Arrays.asList(args));
        int arguments = stack.getArguments().size();

        for (int i = 0; i < arguments; i++) {
            ArgumentNode<?> argument = stack.getArguments().pop();
            ArgumentContainer<?> argumentContainer = argument.getContainer();
            String identifier = argument.getIdentifier();

            try {
                parsedArgs.put(identifier, argumentContainer.getParser().parse(argument, stack));
            } catch (MissingArgumentException exception) {
                if (argumentContainer.isOptional()) {
                    parsedArgs.put(identifier, new ParsedArgument<>(identifier, argumentContainer.getDefaultValue()));
                } else {
                    exception.setContext(command, i);
                    throw exception;
                }
            } catch (MalformedArgumentException exception) {
                exception.setContext(command, i);
                throw exception;
            }
        }

        return new ParsedArgumentSet(parsedArgs);
    }

    <T> ParsedArgument<?> parse(ArgumentNode<T> currentNode, ArgumentStack stack) throws ArgumentException;
}
