package com.diamondfire.helpbot.bot.command.argument.impl.parsing;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.ArgumentContainer;

// An argument node contains an identifier and a container.
public class ArgumentNode<T> {

    private final String identifier;
    private final ArgumentContainer<T,?> container;

    public ArgumentNode(String identifier, ArgumentContainer<T,?> container) {
        this.identifier = identifier;
        this.container = container;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ArgumentContainer<T,?> getContainer() {
        return container;
    }

}
