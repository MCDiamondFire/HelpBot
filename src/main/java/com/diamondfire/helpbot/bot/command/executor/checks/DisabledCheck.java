package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;

public class DisabledCheck implements CommandCheck {
    @Override
    public boolean check(CommandEvent event) {
        return Permission.ADMINISTRATOR.hasPermission(event.getMember())
                || !CommandHandler.getInstance().getDisabledHandler().isDisabled(event.getCommand());
    }
    
    @Override
    public void buildMessage(CommandEvent event, PresetBuilder builder) {
        builder.withPreset(
                new InformativeReply(InformativeReplyType.ERROR, "Disabled!", "This command has been disabled until further notice.")
        );
        
    }
    
    
}
