package com.diamondfire.helpbot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

public class StringArgument extends Argument<String> {

    @Override
    public String getValue(@NotNull String msg) throws IllegalArgumentException {
        return msg;
    }
}
