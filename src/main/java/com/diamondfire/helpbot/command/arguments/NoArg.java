package com.diamondfire.helpbot.command.arguments;


public class NoArg extends Argument {


    public NoArg() {
        super("", false);
    }

    @Override
    public boolean validate(String msg) {
        return true;
    }

    @Override
    public String failMessage() {
        return null;
    }
}
