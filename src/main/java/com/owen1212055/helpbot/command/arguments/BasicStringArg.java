package com.owen1212055.helpbot.command.arguments;

public class BasicStringArg extends Argument {
    @Override
    public boolean validate(String args) {
        return args.length() != 0;
    }

    @Override
    public String failMessage() {
        return "Text needs to contain more than 0 characters";
    }

    @Override
    public String toString() {
        return "<text>";
    }
}
