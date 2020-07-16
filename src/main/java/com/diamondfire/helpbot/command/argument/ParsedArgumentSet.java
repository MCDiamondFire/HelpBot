package com.diamondfire.helpbot.command.argument;

import com.diamondfire.helpbot.command.argument.impl.ArgumentContainer;
import com.diamondfire.helpbot.command.argument.impl.types.MessageArgument;

import java.util.HashMap;
import java.util.Map;

public class ParsedArgumentSet {

    private final Map<String, ?> arguments = new HashMap<>();

    public ParsedArgumentSet(ArgumentSet set, String[] args) throws IllegalArgumentException {
        int argNum = 0;
        for (Map.Entry<String, ArgumentContainer> map : set.getArguments().entrySet()) {
            ArgumentContainer container = map.getValue();

            if (argNum >= args.length) {
                if (container.getArgument().isOptional()) {
                    arguments.put(map.getKey(), container.getArgument().getDefaultValue());
                    continue;
                }
                throw new IllegalArgumentException(String.format("Expected argument at position %s.", argNum));
            }
            try {
                //TODO Add plural argument handling

                if (container.getArgument() instanceof MessageArgument) {
                    arguments.put(map.getKey(), container.getValue(String.join(" ", args)));
                    return;
                } else {
                    arguments.put(map.getKey(), container.getValue(args[argNum]));
                }

            } catch (IllegalArgumentException exception) {
                if (container.getArgument().isOptional()) {
                    arguments.put(map.getKey(), container.getArgument().getDefaultValue());
                }
                throw new IllegalArgumentException(String.format("Invalid argument at position %s.", argNum));
            }
            argNum++;
        }
    }

    public Map<String, ?> getArguments() {
        return arguments;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String code) {
        return (T) arguments.get(code);
    }
}
