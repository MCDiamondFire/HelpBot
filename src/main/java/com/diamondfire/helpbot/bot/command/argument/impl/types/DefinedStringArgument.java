package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.util.JaroWinkler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DefinedStringArgument extends Argument<String> {

    final String[] options;

    public DefinedStringArgument(@NotNull String... options) {
        super();
        this.options = options;
    }

    @Override
    public String getValue(@NotNull String msg) throws IllegalArgumentException {
        String option = getClosestOption(msg);
        if (option == null)
            throw new IllegalArgumentException("Please pick from the following: " + String.join(", ", options));

        return option;
    }

    private String getClosestOption(String args) {
        //Generate a bunch of "favorable" actions.
        Map<String, Double> possibleChoices = new HashMap<>();
        for (String option : options) {
            possibleChoices.put(option, JaroWinkler.score(args, option));
        }

        //Get the most similar action possible.
        Map.Entry<String, Double> closestAction = possibleChoices.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);

        return closestAction.getValue() >= 0.85 ? closestAction.getKey() : null;
    }
}
