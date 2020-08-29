package com.diamondfire.helpbot.bot.command.argument;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ArgumentNode;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;
import org.jetbrains.annotations.*;

import java.util.*;

public class ArgumentSet {

    private final List<ArgumentNode<?>> arguments = new ArrayList<>();

    //Convenience method
    @Contract("_,_ -> this")
    public ArgumentSet addArgument(@NotNull String name, @NotNull Argument<?> argument) {
        arguments.add(new ArgumentNode<>(name, new SingleArgumentContainer<>(argument)));
        return this;
    }

    @Contract("_,_ -> this")
    public ArgumentSet addArgument(@NotNull String name, @NotNull ArgumentContainer<?,?> argument) {
        arguments.add(new ArgumentNode<>(name, argument));
        return this;
    }


    @Contract(pure = true)
    public List<ArgumentNode<?>> getArguments() {
        return arguments;
    }

}
