package com.diamondfire.helpbot.components.reactions.impl;

import net.dv8tion.jda.api.entities.MessageReaction;

@FunctionalInterface
public interface ReactionResponder {

    void react(ReactionRespondEvent reaction);
}
