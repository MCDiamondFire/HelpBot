package com.diamondfire.helpbot.util;

import net.dv8tion.jda.api.entities.Guild;

import java.util.*;

/**
 * Spaces out requests for all of a guild's members (loadMembers, findMembers, findMembersWithRoles).
 * <p>
 * Since October 2025 Discord allows one such request per guild per bot every 30 seconds, and answers
 * the rest with a RATE_LIMITED event instead of members. JDA 6.7 ignores that event, so the request
 * just waits until it times out. Requests for specific members (retrieveMembersByIds) are not limited.
 *
 * @see <a href="https://docs.discord.com/developers/change-log">Discord changelog, August 14, 2025</a>
 */
public final class MemberLoadGate {

    private static final long SPACING_MILLIS = 31_000;
    private static final Map<Long, Long> NEXT_ALLOWED = new HashMap<>();

    private MemberLoadGate() {
    }

    /**
     * Blocks until this guild's member list may be requested, then reserves that slot.
     * Callers queue up behind each other, so it must not be called from a JDA callback thread.
     */
    public static synchronized void await(Guild guild) {
        long wait = NEXT_ALLOWED.getOrDefault(guild.getIdLong(), 0L) - System.currentTimeMillis();
        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        NEXT_ALLOWED.put(guild.getIdLong(), System.currentTimeMillis() + SPACING_MILLIS);
    }

}
