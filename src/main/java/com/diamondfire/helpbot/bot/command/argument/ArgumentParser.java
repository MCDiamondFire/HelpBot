package com.diamondfire.helpbot.bot.command.argument;

import com.diamondfire.helpbot.bot.command.argument.impl.ArgumentContainer;
import com.diamondfire.helpbot.bot.command.argument.impl.types.MessageArgument;

import java.util.*;

public class ArgumentParser {

    public Map<String, ?> parse(ArgumentSet set, String[] args) {
        Map<String, ?> arguments = new HashMap<>();
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
                    return arguments;
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
        return arguments;
    }
}
