package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;

public class PermissionCheck implements CommandCheck {
    
    @Override
    public boolean check(CommandEvent event) {
        return event.getBaseCommand().getPermission().hasPermission(event.getMember())
            || Permission.getOverrides(event.getBaseCommand()).contains(event.getAuthor().getIdLong());
    }
    
    @Override
    public PresetBuilder buildMessage(CommandEvent event) {
        PresetBuilder builder = new PresetBuilder();

        builder.withPreset(
                new InformativeReply(InformativeReplyType.ERROR, "No Permission!", "Sorry, you do not have permission to use this command. Commands that you are able to use are listed in ?help.")
        );
        builder.getEmbed().setFooter("Permission Required: " + event.getBaseCommand().getPermission().name());

        return builder;
    }
    
    
}
