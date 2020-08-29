package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.MultiArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

import java.util.*;

public class MultiArgumentParser<A> extends ArgumentParser<MultiArgumentContainer<A>, A> {

    public MultiArgumentParser(MultiArgumentContainer<A> container) {
        super(container);
    }

    @Override
    public ParsedArgument<?> parse(String identifier, ArgumentStack stack) throws ArgumentException {
        Deque<String> args = stack.getRawArguments();
        List<A> approvedArgumentValues = new ArrayList<>();
        Argument<A> arg = getContainer().getArgument();
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
            throw new MissingArgumentException("No valid arguments were provided.");
        }

        return new ParsedArgument<>(identifier, approvedArgumentValues);
    }


}
