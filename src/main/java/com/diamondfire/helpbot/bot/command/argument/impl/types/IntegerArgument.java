package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import org.jetbrains.annotations.NotNull;

public class IntegerArgument implements Argument<Integer> {

    @Override
    public Integer parseValue(@NotNull String msg) throws ArgumentException {
        try {
            return Integer.parseInt(msg);
        } catch (NumberFormatException exception) {
            throw new MalformedArgumentException("Invalid number provided");
        }
    }
}
