package com.diamondfire.helpbot.command.arguments.value;

import com.diamondfire.helpbot.command.arguments.value.ValueArgument;

public class IntegerArg extends ValueArgument<Integer> {


    public IntegerArg(String name, boolean isRequired) {
        super(name, isRequired);
    }

    public IntegerArg(String name, Integer fallbackValue) {
        super(name, fallbackValue);
    }

    @Override
    public Integer getValue(String msg) {
        return Integer.parseInt(msg);
    }


    @Override
    protected boolean validateValue(String msg) {
        try {
            Integer.parseInt(msg);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }


    @Override
    public String failMessage() {
        return "Text must contain a valid integer!";
    }

    @Override
    public String toString() {
        return "<Optional Number>";
    }
}
