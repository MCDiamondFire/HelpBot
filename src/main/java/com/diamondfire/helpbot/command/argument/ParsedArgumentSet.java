package com.diamondfire.helpbot.command.argument;

import java.util.Map;

public class ParsedArgumentSet {

    private final Map<String, ?> arguments;

    public ParsedArgumentSet(ArgumentSet set, String[] args) throws IllegalArgumentException {
        arguments = new ArgumentParser().parse(set, args);
    }

    public Map<String, ?> getArguments() {
        return arguments;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String code) {
        return (T) arguments.get(code);
    }
}
