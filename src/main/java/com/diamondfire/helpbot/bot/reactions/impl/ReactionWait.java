package com.diamondfire.helpbot.bot.reactions.impl;

import net.dv8tion.jda.api.JDA;

import java.util.TimerTask;

public class ReactionWait extends TimerTask {

    private final JDA jda;
    private final long user;
    private final long message;
    private final long channel;
    private final ReactionResponder responder;
    private final boolean multiUse;

    public ReactionWait(JDA jda, long user, long channel, long message, ReactionResponder responder) {
        this.jda = jda;
        this.user = user;
        this.message = message;
        this.channel = channel;
        this.responder = responder;
        this.multiUse = false;
    }

    public ReactionWait(JDA jda, long user, long channel, long message, ReactionResponder responder, boolean multiUse) {
        this.jda = jda;
        this.user = user;
        this.message = message;
        this.channel = channel;
        this.responder = responder;
        this.multiUse = multiUse;
    }

    @Override
    public void run() {
        jda.getTextChannelById(this.channel).retrieveMessageById(this.message).queue((message) -> message.clearReactions().queue());
    }

    public long getUser() {
        return user;
    }

    public long getChannel() {
        return channel;
    }

    public long getMessage() {
        return message;
    }

    public ReactionResponder getResponder() {
        return responder;
    }

    public boolean isMultiUse() {
        return multiUse;
    }
}
