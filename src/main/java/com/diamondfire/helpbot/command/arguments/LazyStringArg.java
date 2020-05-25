package com.diamondfire.helpbot.command.arguments;

public class LazyStringArg extends ValueArgument<String> {

    @Override
    public String getArg(String msg) {
        return msg;
    }

    @Override
    public boolean validate(String msg) {
        return true;
    }

    @Override
    public String failMessage() {
        return "If you are seeing this message, you screwed something up lol.";
    }

    @Override
    public String toString() {
        return "<optional text>";
    }
}
