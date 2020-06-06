package com.diamondfire.helpbot.components.reactions.impl;

import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionRespondEvent {
    private ReactionWait wait;
    private MessageReaction event;

    public ReactionRespondEvent(ReactionWait wait, MessageReaction event) {
        this.wait = wait;
        this.event = event;
    }

    public ReactionWait getReactionWait() {
        return wait;
    }

    public MessageReaction getReactionEvent() {
        return event;
    }
}
