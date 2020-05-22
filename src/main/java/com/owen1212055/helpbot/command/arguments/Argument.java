package com.owen1212055.helpbot.command.arguments;

public abstract class Argument {

    public abstract boolean validate(String args);

    public abstract String failMessage();
}
