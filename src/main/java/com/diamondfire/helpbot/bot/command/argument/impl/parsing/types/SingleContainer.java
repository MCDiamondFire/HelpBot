package com.diamondfire.helpbot.bot.command.argument.impl.parsing.types;

import com.diamondfire.helpbot.bot.command.argument.impl.types.Argument;

public abstract class SingleContainer<T> extends ArgumentContainer<T> {
    
    private final Argument<T> argument;
    
    public SingleContainer(Argument<T> argument) {
        this.argument = argument;
    }
    
    public Argument<T> getArgument() {
        return argument;
    }
}
