package com.diamondfire.helpbot.bot.command.argument.impl.types.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

public class StringArgument extends AbstractSimpleValueArgument<String> {
    
    @Override
    public String parse(@NotNull String msg, CommandEvent event) {
        return msg;
    }
}
