package com.diamondfire.helpbot.bot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

public class StringArgument extends Argument<String> {

    @Override
    public String parseValue(@NotNull String msg) throws IllegalArgumentException {
        return msg;
    }
}
