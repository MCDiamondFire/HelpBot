package com.diamondfire.helpbot.command.arguments;

public class BasicStringArg extends ValueArgument<String> {

    @Override
    public String getArg(String msg) {
        return msg;
    }

    @Override
    public boolean validate(String msg) {
        return msg.length() > 0;
    }

    @Override
    public String failMessage() {
        return "Text needs to contain more than 0 characters";
    }
}
