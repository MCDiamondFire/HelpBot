package com.diamondfire.helpbot.command.arguments;

public abstract class Argument {
    // Bad and ugly implementation

    public boolean validate(String msg) {
        return true;
    }

    public String failMessage() {
        return "";
    }
}
