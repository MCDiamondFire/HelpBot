package com.diamondfire.helpbot.sys.reactions.impl;

import net.dv8tion.jda.api.entities.MessageReaction;

public class ReactionRespondEvent {

    private final ReactionWait wait;
    private final MessageReaction event;

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
