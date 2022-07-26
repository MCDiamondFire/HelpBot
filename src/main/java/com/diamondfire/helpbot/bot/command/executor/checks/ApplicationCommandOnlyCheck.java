package com.diamondfire.helpbot.bot.command.executor.checks;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReply;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.InformativeReplyType;
import com.diamondfire.helpbot.bot.events.command.ApplicationCommandEvent;
import com.diamondfire.helpbot.bot.events.command.CommandEvent;

public class ApplicationCommandOnlyCheck implements CommandCheck {
    @Override
    public boolean check(CommandEvent event) {
        return !(event instanceof ApplicationCommandEvent) || event.getBaseCommand().supportsSlashCommands();
    }

    @Override
    public PresetBuilder buildMessage(CommandEvent event) {
        return new PresetBuilder().withPreset(
            new InformativeReply(InformativeReplyType.ERROR, "This command can only be run through a message!")
        );
    }
}
