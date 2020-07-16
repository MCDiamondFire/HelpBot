package com.diamondfire.helpbot.components.reactions.impl;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.HashMap;
import java.util.Timer;

public class ReactionHandler {

    private static final HashMap<Long, ReactionWait> reactionWaitHashMap = new HashMap<>();

    private static final Timer timer = new Timer();

    public static void waitReaction(long user, Message message, ReactionResponder responder) {
        if (isWaiting(user)) {
            reactionWaitHashMap.get(user).run();
        }

        ReactionWait wait = new ReactionWait(user, message.getChannel().getIdLong(), message.getIdLong(), responder);
        timer.schedule(wait, 50000);

        reactionWaitHashMap.put(user, wait);

    }

    public static void waitReaction(long user, Message message, ReactionResponder responder, boolean multiUse) {
        if (isWaiting(user)) {
            reactionWaitHashMap.get(user).run();
        }

        ReactionWait wait = new ReactionWait(user, message.getChannel().getIdLong(), message.getIdLong(), responder, multiUse);
        timer.schedule(wait, 50000);

        reactionWaitHashMap.put(user, wait);

    }

    public static boolean isWaiting(long user) {
        return reactionWaitHashMap.containsKey(user);
    }

    public static boolean isMessageReserved(long message) {
        return reactionWaitHashMap.values().stream()
                .anyMatch((reactionWait -> reactionWait.getMessage() == message));
    }

    public static void reacted(Member user, long messageID, MessageReaction event) {
        ReactionWait wait = reactionWaitHashMap.get(user.getIdLong());
        if (wait.getMessage() == messageID) {
            wait.cancel();
            if (wait.isMultiUse()) {
                //Remake wait instance
                ReactionWait waitNew = new ReactionWait(wait.getUser(), wait.getChannel(), wait.getMessage(), wait.getResponder(), wait.isMultiUse());
                timer.schedule(waitNew, 50000);
                reactionWaitHashMap.put(user.getIdLong(), waitNew);
                wait = waitNew;
            }
            wait.getResponder().react(new ReactionRespondEvent(wait, event));
        }
    }

}
