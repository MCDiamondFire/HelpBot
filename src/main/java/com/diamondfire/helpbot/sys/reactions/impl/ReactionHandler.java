package com.diamondfire.helpbot.sys.reactions.impl;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ReactionHandler {

    private static final HashMap<Long, ScheduledReactionTask> reactionWaitHashMap = new HashMap<>();
    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    private static final int DELAY = 5;
    private static final TimeUnit DELAY_UNIT = TimeUnit.MINUTES;

    public static void waitReaction(long user, Message message, Consumer<ReactionRespondEvent> responder) {
        scheduleNew(new ReactionWait(message, user, responder));
    }

    public static void waitReaction(long user, Message message, Consumer<ReactionRespondEvent> responder, boolean persistent) {
        scheduleNew(new ReactionWait(message, user, responder, persistent));
    }

    private static void scheduleNew(ReactionWait wait) {
        long user = wait.getUser();
        if (isWaiting(user)) {
            ScheduledReactionTask scheduledWait = reactionWaitHashMap.get(user);
            scheduledWait.getWait().run();
            scheduledWait.getFuture().cancel(false);
        }

        schedule(user, wait);
    }

    private static void schedule(long user, ReactionWait wait) {
        reactionWaitHashMap.put(user, new ScheduledReactionTask(service.schedule(wait, DELAY, DELAY_UNIT), wait));
    }

    public static boolean isWaiting(long user) {
        return reactionWaitHashMap.containsKey(user);
    }

    public static boolean isMessageReserved(long message) {
        for (ScheduledReactionTask wait : reactionWaitHashMap.values()) {
            if (wait.getWait().getMessage().getIdLong() == message) {
                return true;
            }
        }
        return false;
    }

    public static void handleReaction(GuildMessageReactionAddEvent event) {
        long messageID = event.getMessageIdLong();
        User user = event.getUser();
        ScheduledReactionTask task = reactionWaitHashMap.get(user.getIdLong());
        ReactionWait reactionWait = task.getWait();

        if (reactionWait.getMessage().getIdLong() == messageID) {
            reactionWait.getResponder().accept(new ReactionRespondEvent(reactionWait, event.getReaction()));

            if (reactionWait.isPersistent()) {
                task.getFuture().cancel(false);
                schedule(reactionWait.getUser(), reactionWait);
            } else {
                task.getFuture().cancel(true);
            }
        }
    }

    private static class ScheduledReactionTask {

        private final ScheduledFuture<?> future;
        private final ReactionWait wait;

        public ScheduledReactionTask(ScheduledFuture<?> future, ReactionWait wait) {
            this.future = future;
            this.wait = wait;
        }

        public ScheduledFuture<?> getFuture() {
            return future;
        }

        public ReactionWait getWait() {
            return wait;
        }
    }

}
