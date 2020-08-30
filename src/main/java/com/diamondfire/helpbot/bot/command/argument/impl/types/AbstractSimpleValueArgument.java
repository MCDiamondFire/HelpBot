package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ArgumentStack;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public abstract class AbstractSimpleValueArgument<T> implements Argument<T> {

    @Override
    public T parseValue(@NotNull ArgumentStack stack) throws ArgumentException {
        try {
            Deque<String> args = stack.getRawArguments();
            T value = parse(args.peek());
            args.pop();
            return value;
        } catch (Exception e) {
            throw e;
        }
    }

    protected abstract T parse(@NotNull String argument) throws ArgumentException;

}
