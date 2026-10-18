package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class SuggestingArgument<T> implements Argument<T> {

    private final Argument<T> argument;
    private final Function<String, List<String>> suggestions;

    public SuggestingArgument(@NotNull Argument<T> argument, @NotNull Function<String, List<String>> suggestions) {
        this.argument = argument;
        this.suggestions = suggestions;
    }

    @Override
    public T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        return argument.parseValue(args, event);
    }

    @Override
    public T parseSlash(@NotNull OptionMapping option, CommandEvent event) throws ArgumentException {
        return argument.parseSlash(option, event);
    }

    @Override
    public OptionData createOptionData(@NotNull String name, @NotNull String description, boolean required) {
        return argument.createOptionData(name, description, required);
    }

    @Override
    public List<String> getCompletions(@NotNull String input) {
        return suggestions.apply(input);
    }

    @Override
    public boolean hasCompletions() {
        return true;
    }
}
