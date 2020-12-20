package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class PermissionCheck implements CommandCheck {
    
    @Override
    public boolean check(CommandEvent event) {
        return event.getCommand().getPermission().hasPermission(event.getMember());
    }
    
    @Override
    public void buildMessage(CommandEvent event, PresetBuilder builder) {
        builder.withPreset(
                new InformativeReply(InformativeReplyType.ERROR, "No Permission!", "Sorry, you do not have permission to use this command. Commands that you are able to use are listed in ?help.")
        );
        builder.getEmbed().setFooter("Permission Required: " + event.getCommand().getPermission().name());
    }
    
    
}
