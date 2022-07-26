package com.diamondfire.helpbot.bot.events.command;

import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.ApiStatus;

public class MessageCommandEvent extends TextCommandEvent {
    private final Message message;
    
    public MessageCommandEvent(Message message) {
        super(message.getTextChannel(), message.getMember(), message.getContentRaw().split(" "));

        this.message = message;
    }

    @ApiStatus.Internal
    public Message getMessage() {
        return message;
    }
}
