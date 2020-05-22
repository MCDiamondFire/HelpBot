package com.diamondfire.helpbot.command.commands;

import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.command.arguments.Argument;

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
