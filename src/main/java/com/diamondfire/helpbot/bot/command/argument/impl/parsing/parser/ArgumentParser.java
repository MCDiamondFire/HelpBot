package com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.types.ArgumentContainer;
import com.diamondfire.helpbot.bot.command.impl.Command;

import java.util.*;

// Argument parsers give a ParsedArgument from a ArgumentContainer, that's all.
public abstract class ArgumentParser<T extends ArgumentContainer<A>, A> {
    
    private final T container;
    
    public ArgumentParser(T container) {
        this.container = container;
    }
    
    public static ParsedArgumentSet parseArgs(Command command, String[] args) throws ArgumentException {
        Map<String, ParsedArgument<?>> parsedArgs = new HashMap<>();
        ArgumentStack stack = new ArgumentStack(command.getArguments().getArguments(), Arrays.asList(args));
        int arguments = stack.getArguments().size();
        
        for (int i = 0; i < arguments; i++) {
            ArgumentStack.RawArgumentStack rawArguments = stack.getRawArguments();
            ArgumentNode<?> argument = stack.getArguments().pop();
            ArgumentContainer<?> argumentContainer = argument.getContainer();
            String identifier = argument.getIdentifier();
            
            try {
                parsedArgs.put(identifier, argumentContainer.getParser().parse(identifier, rawArguments));
                rawArguments.pushStack();
            } catch (MissingArgumentException exception) {
                if (argumentContainer.isOptional()) {
                    parsedArgs.put(identifier, new ParsedArgument<>(identifier, argumentContainer.getDefaultValue()));
                } else {
                    exception.setContext(command, i);
                    throw exception;
                }
            } catch (MalformedArgumentException exception) {
                exception.setContext(command, i);
                throw exception;
            }
        }
        
        return new ParsedArgumentSet(parsedArgs);
    }
    
    public abstract ParsedArgument<?> parse(String identifier, ArgumentStack.RawArgumentStack args) throws ArgumentException;
    
    protected T getContainer() {
        return container;
    }
}
