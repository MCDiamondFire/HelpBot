package com.diamondfire.helpbot.bot.command.argument.impl.parsing;


// Parsed arguments are simply just their identifier and their value. (Finalized and parsed)
public class ParsedArgument<T> {
    
    String argumentIdentifier;
    T argument;
    
    public ParsedArgument(String argumentIdentifier, T argument) {
        this.argument = argument;
        this.argumentIdentifier = argumentIdentifier;
    }
    
    public T getValue() {
        return argument;
    }
    
}
