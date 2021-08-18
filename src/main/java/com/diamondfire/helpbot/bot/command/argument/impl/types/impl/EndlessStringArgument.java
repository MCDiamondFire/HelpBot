package com.diamondfire.helpbot.bot.command.argument.impl.types.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public class EndlessStringArgument extends Argument<String> {
    
    @Override
    public String parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return String.join(" ", args);
    }
}
