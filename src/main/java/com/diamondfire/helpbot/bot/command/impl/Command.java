package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.CommandEvent;

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
