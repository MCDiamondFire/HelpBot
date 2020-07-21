package com.diamondfire.helpbot.bot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

public class MessageArgument extends Argument<String> {

    @Override
    public String getValue(@NotNull String msg) throws IllegalArgumentException {
        return msg;
    }
}
