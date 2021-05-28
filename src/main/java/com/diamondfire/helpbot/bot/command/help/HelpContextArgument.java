package com.diamondfire.helpbot.bot.command.help;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class HelpContextArgument {
    
    String argumentName = null;
    boolean isOptional = false;
    OptionType type = OptionType.STRING;
    
    public HelpContextArgument name(String argumentName) {
        this.argumentName = argumentName;
        return this;
    }
    
    public HelpContextArgument type(OptionType type) {
        this.type = type;
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
    
    public OptionType getType() {
        return type;
    }
}
