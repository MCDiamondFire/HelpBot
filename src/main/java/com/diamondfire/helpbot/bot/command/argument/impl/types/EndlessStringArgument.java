package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public class EndlessStringArgument implements Argument<String> {
    
    @Override
    public String parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return String.join(" ", args);
    }
    
    @Override
    public String parseSlash(@NotNull OptionMapping option, CommandEvent event) {
        return option.getAsString();
    }
}
