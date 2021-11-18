package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.argument.ArgumentSet;
import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;

public abstract class Command {
    
    private ArgumentSet set = null;
    
    public abstract String getName();
    
    public boolean cacheArgumentSet() {
        return true;
    }
    
    public boolean supportsSlashCommands() {
        return true;
    }
    
    public String[] getAliases() {
        return new String[0];
    }
    
    public abstract HelpContext getHelpContext();
    
    protected abstract ArgumentSet compileArguments();
    
    public ArgumentSet getArguments() {
        if (cacheArgumentSet()) {
            if (set == null) {
                set = compileArguments();
            }
            return set;
        } else {
            return compileArguments();
        }
    }
    
    public abstract Permission getPermission();
    
    public abstract void run(CommandEvent event);
}
