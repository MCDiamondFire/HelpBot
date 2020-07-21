package com.diamondfire.helpbot.bot.reactions.impl;

@FunctionalInterface
public interface ReactionResponder {

    void react(ReactionRespondEvent reaction);
}
