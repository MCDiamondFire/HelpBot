package com.diamondfire.helpbot.bot.command.argument.impl.parsing;

import java.util.*;

// ArgumentStacks contain a bunch of argument nodes and raw arguments.
public class ArgumentStack {

    private final Deque<String> rawArgs;
    private final Deque<ArgumentNode<?>> args;

    public ArgumentStack(List<ArgumentNode<?>> args, Collection<String> rawArgs) {
        this.rawArgs = new ArrayDeque<>(rawArgs);
        this.args = new ArrayDeque<>(args);

    }

    public Deque<String> getRawArguments() {
        return rawArgs;
    }

    public Deque<ArgumentNode<?>> getArguments() {
        return args;
    }
}


