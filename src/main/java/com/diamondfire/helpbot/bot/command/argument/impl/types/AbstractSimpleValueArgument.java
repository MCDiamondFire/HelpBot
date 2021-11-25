package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public abstract class AbstractSimpleValueArgument<T> implements Argument<T> {
    
    @Override
    public T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return parse(args.pop(), event);
    }
    
    @Override
    public T parseSlash(OptionMapping optionMapping, CommandEvent event) throws ArgumentException {
        return parse(optionMapping.getAsString(), event);
    }
    
    public abstract T parse(@NotNull String argument, CommandEvent event) throws ArgumentException;
    
}
