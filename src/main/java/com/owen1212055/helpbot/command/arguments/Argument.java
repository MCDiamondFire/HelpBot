package com.owen1212055.helpbot.command.arguments;

import net.dv8tion.jda.api.EmbedBuilder;

public abstract class Argument {

    public abstract boolean validate(String args);

    public abstract String failMessage();
}
