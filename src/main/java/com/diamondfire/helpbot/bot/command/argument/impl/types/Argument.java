package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Arguments simply parse a given value and can remove it from the stack if they process it correctly.
 * This means an argument can actually use more than one argument if they want!
 *
 * @param <T> parsed value type
 */
public interface Argument<T> {
    
    T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException;
    
    default OptionData createOptionData(@NotNull String name, @NotNull String description, boolean required) {
        return new OptionData(OptionType.STRING, name, description, required);
    }
    
    default T parseSlash(@NotNull OptionMapping option, CommandEvent event) throws ArgumentException {
        return parseValue(new ArrayDeque<>(Arrays.asList(option.getAsString().trim().split("\\s+"))), event);
    }
    
    default List<String> getCompletions(@NotNull String input) {
        return List.of();
    }
    
    default boolean hasCompletions() {
        return false;
    }
}

