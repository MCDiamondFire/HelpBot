package com.diamondfire.helpbot.bot.reactions.impl;

import net.dv8tion.jda.api.entities.Message;

import java.util.function.Consumer;

public class ReactionWait implements Runnable {

    private final long user;
    private final Message message;
    private final Consumer<ReactionRespondEvent> responder;
    private final boolean persistent;

    public ReactionWait(Message message, long user, Consumer<ReactionRespondEvent> responder) {
        this.message = message;
        this.user = user;
        this.responder = responder;
        this.persistent = false;
    }


    public ReactionWait(Message message, long user, Consumer<ReactionRespondEvent> responder, boolean persistent) {
        this.message = message;
        this.user = user;
        this.responder = responder;
        this.persistent = persistent;
    }

    @Override
    public void run() {
        message.clearReactions().queue();
    }

    public long getUser() {
        return user;
    }

    public Message getMessage() {
        return message;
    }

    public Consumer<ReactionRespondEvent> getResponder() {
        return responder;
    }

    public boolean isPersistent() {
        return persistent;
    }

}
