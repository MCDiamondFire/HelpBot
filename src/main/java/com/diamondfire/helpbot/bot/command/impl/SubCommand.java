package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.command.help.HelpContext;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.SubCommandEvent;

public interface SubCommand {
    
    String getName();
    
    HelpContext getHelpContext();
    
    Permission getPermission();
    
    void run(SubCommandEvent event);
    
}
