package com.diamondfire.helpbot.bot.command.argument.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;
import org.jetbrains.annotations.NotNull;

// This is just a wrapper class so I don't have to deal with <> and raw usage warnings everywhere.
public class ArgumentContainer {

    private final Argument<?> argument;

    public ArgumentContainer(@NotNull Argument<?> argument) {
        this.argument = argument;
    }

    public Argument<?> getArgument() {
        return argument;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String msg) {
        return (T) getArgument().getValue(msg);
    }

}
