package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.SubCommandEvent;

public abstract class SubCommand {
    
    public abstract String getName();
    
    public abstract HelpContext getHelpContext();
    
    public abstract Permission getPermission();
    
    public abstract void run(SubCommandEvent event);
    
}
