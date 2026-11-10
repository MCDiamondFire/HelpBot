package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class IntegerArgument extends AbstractSimpleValueArgument<Integer> {
    
    @Override
    public OptionData createOptionData(@NotNull String name, @NotNull String description, boolean required) {
        return new OptionData(OptionType.INTEGER, name, description, required).setRequiredRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    @Override
    public Integer parse(@NotNull String msg, CommandEvent event) throws ArgumentException {
        try {
            return Integer.parseInt(msg);
        } catch (NumberFormatException exception) {
            throw new MalformedArgumentException("Invalid number provided");
        }
    }
}
