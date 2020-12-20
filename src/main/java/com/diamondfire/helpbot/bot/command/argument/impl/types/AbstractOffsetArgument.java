package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractOffsetArgument extends AbstractSimpleValueArgument<Date> {
    
    @Override
    protected Date parse(@NotNull String argument) throws ArgumentException {
        Calendar calendar = Calendar.getInstance();
        int offset = 0;
        boolean modified = false;
        
        Map<Character, Integer> durationMap = new HashMap<>();
        for (Duration duration : getDurations()) {
            durationMap.put(duration.getId(), duration.getUnit());
        }
        
        for (int i = 0; i < argument.length(); i++) {
            char currentChar = argument.charAt(i);
            
            if (!Character.isDigit(currentChar)) {
                if (durationMap.containsKey(currentChar)) {
                    calendar.add(durationMap.get(currentChar), Integer.parseInt(argument.substring(offset, i)));
                    modified = true;
                } else {
                    List<String> units = new ArrayList<>();
                    for (Character character : durationMap.keySet()) {
                        units.add(character.toString());
                    }
                    
                    throw new MalformedArgumentException("Unknown time unit: " + currentChar + " \nPossible time units are: " + String.join(",", units));
                }
                offset = i + 1;
            }
        }
        
        if (!modified) {
            throw new MalformedArgumentException("Malformed duration!");
        }
        
        return calendar.getTime();
    }
    
    protected abstract Duration[] getDurations();
    
    public static class Duration {
        
        private final int unit;
        private final char id;
        
        public Duration(int unit, char id) {
            this.id = id;
            this.unit = unit;
        }
        
        public int getUnit() {
            return unit;
        }
        
        public char getId() {
            return id;
        }
    }
    
    
}
