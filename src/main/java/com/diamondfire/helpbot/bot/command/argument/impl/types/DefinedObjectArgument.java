package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.util.JaroWinkler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DefinedObjectArgument<T> implements Argument<T> {

    private final Map<String, T> objectMap = new HashMap<>();

    public DefinedObjectArgument(@NotNull T... options) {

        for (T option : options) {
            objectMap.put(option.toString(), option);
        }
    }

    @Override
    public T parseValue(@NotNull String msg) throws ArgumentException {
        T option = getClosestOption(msg);
        if (option == null) {
            throw new MalformedArgumentException("Please pick from the given list: " + String.join(", ", objectMap.keySet()));
        }


        return option;
    }

    private T getClosestOption(String args) {
        //Generate a bunch of "favorable" actions.
        Map<String, Double> possibleChoices = new HashMap<>();
        for (String option : objectMap.keySet()) {
            possibleChoices.put(option, JaroWinkler.score(args, option));
        }

        //Get the most similar action possible.
        Map.Entry<String, Double> closestAction = possibleChoices.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);

        return closestAction.getValue() >= 0.85 ? objectMap.get(closestAction.getKey()) : null;
    }
}
