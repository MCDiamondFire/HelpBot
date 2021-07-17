package com.diamondfire.helpbot.bot.command.impl.other.tag;

public class TagDoesntExistException extends Exception {
    public TagDoesntExistException(String message) {
        super(message);
    }
}
