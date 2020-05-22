package com.owen1212055.helpbot.command.arguments;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NoArg extends Argument{
    @Override
    public boolean validate(String args) {
        return true;
    }

    @Override
    public String failMessage() {
        return "If you are seeing this message, you screwed something up lol.";
    }

    @Override
    public String toString() {
        return "";
    }
}
