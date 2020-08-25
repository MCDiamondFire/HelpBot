package com.diamondfire.helpbot.bot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

public class StringArgument implements Argument<String> {

    @Override
    public String parseValue(@NotNull String msg) {
        return msg;
    }
}
