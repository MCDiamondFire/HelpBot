package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;

// Command checks are run before a command is executed.
public interface CommandCheck {
    
    boolean check(CommandEvent event);
    
    void buildMessage(CommandEvent event, PresetBuilder builder);
}
