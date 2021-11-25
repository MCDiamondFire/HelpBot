package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuoteStringArgument implements Argument<String> {
    
    private static final String ERROR_MESSAGE = "Invalid quote, must start and end with `'` or `\"`.";
    
    @Override
    public String parseValue(@NotNull Deque<String> args, CommandEvent event) throws ArgumentException {
    
        // Check if argument starts with quote
        if (!(args.element().startsWith("'") || args.peek().startsWith("\""))) {
            throw new MalformedArgumentException(ERROR_MESSAGE);
        }
    
        // Init variables
        List<String> elements = new ArrayList<>();
        String argument = args.poll();
        elements.add(argument);
    
        // Repeat arguments until end is reached (exception) or an end quote is found
        while (!(argument.endsWith("'") || argument.endsWith("\""))) {
            argument = args.poll();
            
            if (argument == null) {
                throw new MalformedArgumentException(ERROR_MESSAGE);
            }
        
            elements.add(argument);
        }
    
        // Join elements and remove quotes
        String result = String.join(" ", elements);
        return result.substring(1, result.length()-1);
    }
    
    @Override
    public String parseSlash(OptionMapping optionMapping, CommandEvent event) throws ArgumentException {
        return optionMapping.getAsString();
    }
}
