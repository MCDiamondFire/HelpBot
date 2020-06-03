package com.diamondfire.helpbot.command.arguments.value;

public class StringArg extends ValueArgument<String> {


    public StringArg(String name, boolean isRequired) {
        super(name, isRequired);
    }

    public StringArg(String name, String fallbackValue) {
        super(name, fallbackValue);
    }

    @Override
    public String getValue(String msg) {
        return msg;
    }

    @Override
    protected boolean validateValue(String msg) {
        return isRequired();
    }

}
