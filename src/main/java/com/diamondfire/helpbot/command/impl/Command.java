package com.diamondfire.helpbot.command.impl;

import com.diamondfire.helpbot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.command.help.HelpContext;
import com.diamondfire.helpbot.command.permissions.Permission;
import com.diamondfire.helpbot.events.CommandEvent;

public abstract class Command {

    public abstract String getName();

    public String[] getAliases() {
        return new String[0];
    }

    public abstract HelpContext getHelpContext();

    public abstract ArgumentSet getArguments();

    public abstract Permission getPermission();

    public abstract void run(CommandEvent event);
}
