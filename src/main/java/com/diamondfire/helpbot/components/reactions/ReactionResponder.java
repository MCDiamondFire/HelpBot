package com.diamondfire.helpbot.components.reactions;

import net.dv8tion.jda.api.entities.MessageReaction;

public interface ReactionResponder {

    void react(MessageReaction reaction);
}
