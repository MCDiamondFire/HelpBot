package com.owen1212055.helpbot.command.commands;

import com.owen1212055.helpbot.command.arguments.Argument;
import com.owen1212055.helpbot.command.permissions.Permission;
import com.owen1212055.helpbot.events.CommandEvent;

public abstract class Command {
    public abstract String getName();

    public abstract String getDescription();

    public abstract Argument getArgument();

    public abstract Permission getPermission();

    protected boolean inHelp() {
        return true;
    }
    public abstract void run(CommandEvent event);
}
