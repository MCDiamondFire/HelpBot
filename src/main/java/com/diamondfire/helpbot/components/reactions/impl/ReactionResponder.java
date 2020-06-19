package com.diamondfire.helpbot.components.reactions.impl;

@FunctionalInterface
public interface ReactionResponder {

    void react(ReactionRespondEvent reaction);
}
