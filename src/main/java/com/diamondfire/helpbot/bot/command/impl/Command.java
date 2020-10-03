package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public abstract class Command {

    private final ArgumentSet set = compileArguments();

    public abstract String getName();

    public String[] getAliases() {
        return new String[0];
    }

    public abstract HelpContext getHelpContext();

    protected abstract ArgumentSet compileArguments();

    public ArgumentSet getArguments() {
        return set;
    }

    public abstract Permission getPermission();

    public abstract void run(CommandEvent event);
}
