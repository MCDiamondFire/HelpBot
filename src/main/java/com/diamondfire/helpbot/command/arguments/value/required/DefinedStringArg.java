package com.diamondfire.helpbot.command.arguments.value.required;


import com.diamondfire.helpbot.command.arguments.value.ValueArgument;
import com.diamondfire.helpbot.util.JaroWinkler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DefinedStringArg extends ValueArgument<String> {

    String[] options;

    public DefinedStringArg(String[] options) {
        this.options = options;
    }


    @Override
    public String getArg(String msg) {
        return getClosestOption(msg);
    }

    @Override
    public boolean validate(String msg) {
        return getClosestOption(msg) != null;
    }

    @Override
    public String failMessage() {
        return "Invalid argument, please choose from the following: " + String.join(", ", options);
    }

    @Override
    public String toString() {
        String parsedOptions;

        if (options.length > 6) {
            parsedOptions = String.join("/", Arrays.copyOfRange(options, 1, 6)) + "..." + String.format("*+%s more*", options.length - 6);
        } else {
            parsedOptions = String.join("/", options);
        }

        return "<" + parsedOptions + ">";
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
