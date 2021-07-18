package com.diamondfire.helpbot.bot.command.impl;

import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.List;

public abstract class SubCommandReceiver extends Command {
    
    public abstract List<SubCommand> getSubCommands();
    
    public String getFormattedSubCommands(CommandEvent event) {
        String result = "";
        for (SubCommand subcommand : getSubCommands()) {
            if (subcommand.getPermission().hasPermission(event.getMember())) {
                result += "`" + subcommand.getName() + "` ";
            }
        }
        return result;
    }
}
