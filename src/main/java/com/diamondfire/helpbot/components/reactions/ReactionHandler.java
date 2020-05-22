package com.diamondfire.helpbot.components.reactions;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.HashMap;
import java.util.Timer;

public class ReactionHandler {
    private static final HashMap<Long, ReactionWait> reactionWaitHashMap = new HashMap<>();

    private static final Timer timer = new Timer();

    public static void handleReaction(long user, Message message, ReactionResponder responder) {
        if (isWaiting(user)) {
            reactionWaitHashMap.get(user).run();
        }

        ReactionWait wait = new ReactionWait(user, message.getChannel().getIdLong(), message.getIdLong(), responder);
        timer.schedule(wait, 20000);

        reactionWaitHashMap.put(user, wait);

    }

    public static boolean isWaiting(long user) {
        return reactionWaitHashMap.containsKey(user);
    }

    public static boolean isMessageReserved(long message) {
        return reactionWaitHashMap.values().stream()
                .anyMatch((reactionWait -> reactionWait.getMessage() == message));
    }

    public static void reacted(long user, long messageID, MessageReaction event) {
        ReactionWait wait = reactionWaitHashMap.get(user);

        if (wait.getMessage() == messageID) {
            wait.cancel();
            wait.getResponder().react(event);
        }
    }

}
