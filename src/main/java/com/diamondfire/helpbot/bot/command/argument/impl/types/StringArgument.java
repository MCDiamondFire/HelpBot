package com.diamondfire.helpbot.bot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

public class StringArgument extends AbstractSimpleValueArgument<String> {
    
    @Override
    public String parse(@NotNull String msg) {
        return msg;
    }
}
