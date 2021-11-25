package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Arguments simply parse a given value and can remove it from the stack if they process it correctly.
 * This means an argument can actually use more than one argument if they want!
 *
 * @param <T> parsed value type
 */
public interface Argument<T> {
    
    T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException;
    
    default OptionType optionType() {
        return OptionType.STRING;
    }
    
    default OptionData createOptionData(@Nonnull String name, @Nonnull String description, boolean isRequired) {
        return new OptionData(optionType(), name, description, isRequired);
    }
    
    default T parseSlash(OptionMapping optionMapping, CommandEvent event) throws ArgumentException {
        return parseValue(new ArrayDeque<>(Arrays.asList(optionMapping.getAsString().split(" "))), event);
    }
}

