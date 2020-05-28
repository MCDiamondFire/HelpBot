package com.diamondfire.helpbot.command.arguments.value.required;

import com.diamondfire.helpbot.command.arguments.value.ValueArgument;

public class StringArg extends RequiredValueArgument<String> {


    @Override
    public String getArg(String msg) {
        return msg;
    }

    @Override
    public String failMessage() {
        return "Text needs to contain more than 0 characters";
    }

    @Override
    public String toString() {
        return "<Text>";
    }
}
