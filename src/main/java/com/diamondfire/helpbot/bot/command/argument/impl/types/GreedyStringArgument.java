package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public class GreedyStringArgument implements Argument<String> {
    
    @Override
    public String parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return String.join(" ", args);
    }
    
    @Override
    public String parseSlash(OptionMapping optionMapping, CommandEvent event) throws ArgumentException {
        return optionMapping.getAsString();
    }
}
