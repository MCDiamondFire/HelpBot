package com.owen1212055.helpbot.command.arguments;


import com.owen1212055.helpbot.util.JaroWinkler;

import java.util.*;

public class DefinedStringArg extends Argument{

    String[] options;

    public DefinedStringArg(String[] options) {
        super();
        this.options = options;
    }

    public String getClosestOption(String args) {
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

    @Override
    public boolean validate(String args) {
        return getClosestOption(args) != null;
    }

    @Override
    public String failMessage() {
        return "Invalid argument, please choose from the following: " + String.join(", ", options);
    }

    @Override
    public String toString() {
        String parsedOptions = null;
        if (options.length > 6) {
            parsedOptions = String.format(String.join("/", Arrays.asList(options).subList(1, 6).toArray(new String[0])) + "..." + String.format("*+%s more*", options.length - 6) );
        } else {
            parsedOptions = String.join("/", options);
        }

        return "<" + parsedOptions + ">";
    }
}
