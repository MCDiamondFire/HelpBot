package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.impl.other.mod.MuteCommand;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;

public class MutedCheck implements CommandCheck {
    
    @Override
    public boolean check(CommandEvent event) {
        return !event.getMember().getRoles().contains(event.getGuild().getRoleById(MuteCommand.ROLE_ID));
    }
    
    @Override
    public void buildMessage(CommandEvent event, PresetBuilder builder) {
        builder.withPreset(new InformativeReply(InformativeReplyType.ERROR,
                "You cannot use commands while muted."));
    }
}
