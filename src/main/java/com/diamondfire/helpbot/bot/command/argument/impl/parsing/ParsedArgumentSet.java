package com.diamondfire.helpbot.bot.command.argument.impl.parsing;

import java.util.*;

public class ParsedArgumentSet {

    private final Map<String, ParsedArgument<?>> arguments = new HashMap<>();

    public ParsedArgumentSet(Map<String, ParsedArgument<?>> finalizedArguments) throws IllegalArgumentException {
        for (Map.Entry<String, ParsedArgument<?>> argument : finalizedArguments.entrySet()) {
            arguments.put(argument.getKey(), argument.getValue());
        }
    }

    public Map<String, ?> getArguments() {
        return arguments;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String code) {
        return (T) arguments.get(code).getValue();
    }
}
