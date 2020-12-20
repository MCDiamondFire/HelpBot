package com.diamondfire.helpbot.bot.command.help;

public class HelpContextArgument {
    
    String argumentName = null;
    boolean isOptional = false;
    
    public HelpContextArgument name(String argumentName) {
        this.argumentName = argumentName;
        return this;
    }
    
    public HelpContextArgument optional() {
        isOptional = true;
        return this;
    }
    
    public String getArgumentName() {
        return argumentName;
    }
    
    public boolean isOptional() {
        return isOptional;
    }
    
}
