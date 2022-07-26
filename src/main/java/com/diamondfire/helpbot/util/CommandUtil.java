package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.bot.events.command.*;

// This is for more obscure command type specific actions.
public class CommandUtil {
    
    public static void replyEphemeralOrDeleteCommand(CommandEvent event, String content) {
        if (event instanceof MessageCommandEvent messageCommandEvent) {
            messageCommandEvent.getMessage().delete().queue();
        } else if (event instanceof ApplicationCommandEvent applicationCommandEvent) {
            applicationCommandEvent.getInternalEvent()
                    .reply(content)
                    .setEphemeral(true)
                    .queue();
        }
    }
}
