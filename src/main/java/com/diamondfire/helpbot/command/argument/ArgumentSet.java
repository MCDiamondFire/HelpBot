package com.diamondfire.helpbot.command.argument;

import com.diamondfire.helpbot.command.argument.impl.ArgumentContainer;
import com.diamondfire.helpbot.command.argument.impl.types.Argument;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ArgumentSet {

    private final Map<String, ArgumentContainer> arguments = new LinkedHashMap<>();

    public ArgumentSet addArgument(@NotNull String name, @NotNull Argument argument) {
        arguments.put(name, new ArgumentContainer(argument));
        return this;
    }

    public Map<String, ArgumentContainer> getArguments() {
        return arguments;
    }

}
