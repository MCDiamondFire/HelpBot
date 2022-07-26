package com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions;

import org.jetbrains.annotations.ApiStatus;

public class MissingArgumentException extends ArgumentException {

    @ApiStatus.Internal
    public MissingArgumentException(String message) {
        super(message);
    }

    public static MissingArgumentException expectedArgument() {
        return new MissingArgumentException("Expected an argument, but got nothing.");
    }

    public static MissingArgumentException noValidArguments() {
        return new MissingArgumentException("No valid arguments were provided.");
    }
    
}
