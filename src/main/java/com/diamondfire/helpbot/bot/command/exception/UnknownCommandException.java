package com.diamondfire.helpbot.bot.command.exception;

public class UnknownCommandException extends Exception {
    public final Level level;
    public final String command;

    public UnknownCommandException(Level level, String command) {
        this.level = level;
        this.command = command;
    }

    public enum Level {
        ROOT,
        SUBCOMMAND,
    }
}
