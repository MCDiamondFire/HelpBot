package com.diamondfire.helpbot.sys.tag;

public class TagDoesntExistException extends Exception {
    public TagDoesntExistException(String message) {
        super(message);
    }
}
