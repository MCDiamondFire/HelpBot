package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

import java.util.*;

public class MultiArgumentParser implements ArgumentParser {

    @Override
    public <T> ParsedArgument<?> parse(ArgumentNode<T> currentNode, ArgumentStack stack) throws IllegalArgumentException {
        Deque<String> args = stack.getRawArguments();
        List<T> approvedArgumentValues = new ArrayList<>();
        Argument<T> arg = currentNode.getContainer().getArgument();
        int arguments = args.size();

        for (int i = 0; i < arguments; i++) {
            String selectedArg = args.peek();
            try {
                approvedArgumentValues.add(arg.parseValue(selectedArg));
                args.pop();
            } catch (Exception e) {
                break;
            }
        }

        if (approvedArgumentValues.isEmpty()) {
            throw new IllegalArgumentException("No valid arguments were provided.");
        }

        return new ParsedArgument<>(currentNode.getIdentifier(), approvedArgumentValues);
    }
}
