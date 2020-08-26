package com.diamondfire.helpbot.bot.command.help;

import java.util.*;

public class HelpContext {

    final List<HelpContextArgument> arguments = new ArrayList<>();
    String description = null;
    CommandCategory commandCategory = null;

    public HelpContext description(String description) {
        this.description = description;
        return this;
    }

    public HelpContext category(CommandCategory commandCategory) {
        this.commandCategory = commandCategory;
        return this;
    }

    public HelpContext addArgument(HelpContextArgument argument) {
        arguments.add(argument);
        return this;
    }

    public HelpContext addArgument(HelpContextArgument... argument) {
        arguments.addAll(Arrays.asList(argument));
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CommandCategory getCommandCategory() {
        return commandCategory;
    }

    public List<HelpContextArgument> getArguments() {
        return arguments;
    }
}
