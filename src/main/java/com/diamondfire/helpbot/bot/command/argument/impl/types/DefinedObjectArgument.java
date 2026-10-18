package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.JaroWinkler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DefinedObjectArgument<T> implements Argument<T> {
    
    private boolean trailing = false;
    private final Map<String, T> objectMap = new HashMap<>();
    
    public DefinedObjectArgument(@NotNull T... options) {
        for (T option : options) {
            objectMap.put(option.toString(), option);
        }
    }
    
    public DefinedObjectArgument(boolean trailing, @NotNull T... options) {
        for (T option : options) {
            objectMap.put(option.toString(), option);
        }
        this.trailing = trailing;
    }
    
    @Override
    public T parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
        String compareString;
        if (trailing) {
            compareString = String.join(" ", args);
            args.clear();
        } else {
            compareString = args.pop();
        }
        
        return compare(compareString);
    }
    
    @Override
    public T parseSlash(@NotNull OptionMapping option, CommandEvent event) throws ArgumentException {
        return compare(option.getAsString());
    }
    
    @Override
    public OptionData createOptionData(@NotNull String name, @NotNull String description, boolean required) {
        OptionData data = Argument.super.createOptionData(name, description, required);
        if (objectMap.size() <= OptionData.MAX_CHOICES) {
            objectMap.keySet().stream().sorted().forEach(option -> data.addChoice(option, option));
        }
        
        return data;
    }
    
    @Override
    public List<String> getCompletions(@NotNull String input) {
        return objectMap.keySet().stream().sorted().toList();
    }
    
    @Override
    public boolean hasCompletions() {
        return objectMap.size() > OptionData.MAX_CHOICES;
    }
    
    private T compare(String compareString) throws ArgumentException {
        T option = getClosestOption(compareString);
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
        
        if (closestAction == null) return null;
        
        return closestAction.getValue() >= 0.85 ? objectMap.get(closestAction.getKey()) : null;
    }
    
    
}
