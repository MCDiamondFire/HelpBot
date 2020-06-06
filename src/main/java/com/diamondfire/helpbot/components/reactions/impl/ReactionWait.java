package com.diamondfire.helpbot.components.reactions.impl;

import com.diamondfire.helpbot.instance.BotInstance;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.TimerTask;

public class ReactionWait extends TimerTask {

    private final long user;
    private final long message;
    private final long channel;
    private final ReactionResponder responder;
    private final boolean multiUse;

    public ReactionWait(long user, long channel, long message, ReactionResponder responder) {
        this.user = user;
        this.message = message;
        this.channel = channel;
        this.responder = responder;
        this.multiUse = false;
    }
    public ReactionWait(long user, long channel, long message, ReactionResponder responder, boolean multiUse) {
        this.user = user;
        this.message = message;
        this.channel = channel;
        this.responder = responder;
        this.multiUse = multiUse;
    }
    @Override
    public void run() {
        BotInstance.getJda().getTextChannelById(this.channel).retrieveMessageById(this.message).queue((message) -> {
            message.clearReactions().queue();
        });
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
