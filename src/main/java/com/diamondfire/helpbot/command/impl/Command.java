package com.diamondfire.helpbot.command.impl;

import com.diamondfire.helpbot.command.arguments.Argument;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;

public abstract class Command {

    public abstract String getName();

    public String[] getAliases() {
        return new String[0];
    }

    public abstract String getDescription();

    public abstract CommandCategory getCategory();

    public abstract Argument getArgument();

    public abstract Permission getPermission();

    public abstract void run(CommandEvent event);
}
