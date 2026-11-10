package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.LoopingTask;
import com.diamondfire.helpbot.sys.vip.VIPRoleHandler;
import net.dv8tion.jda.api.entities.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Gives every VIP the star role for their name color, takes stars from everyone else,
 * and deletes star roles that no VIP needs.
 */
public class VIPStarTask implements LoopingTask {

    @Override
    public long getInitialStart() {
        return 0;
    }

    @Override
    public long getNextLoop() {
        return TimeUnit.MINUTES.toMillis(30L);
    }

    private record Seen(long id, boolean vip, int color, Set<Long> stars) {

    }

    @Override
    public void run() {
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
        if (guild == null) {
            return;
        }

        Set<Long> vipIds = VIPRoleHandler.retrieveVIPs();
        if (vipIds.isEmpty()) {
            // Most likely a failed lookup; acting on it would strip every star.
            System.out.println("No VIPs found, skipping VIP star update.");
            return;
        }

        VIPRoleHandler.syncWithGuild(guild);

        // Only VIPs and members holding a star matter, so only those are kept while members load.
        List<Seen> relevant = Collections.synchronizedList(new ArrayList<>());
        try {
            guild.loadMembers(member -> {
                Set<Long> stars = new HashSet<>();
                for (Role role : member.getRoles()) {
                    if (VIPRoleHandler.isStarRole(role)) {
                        stars.add(role.getIdLong());
                    }
                }

                boolean vip = vipIds.contains(member.getIdLong());
                if (vip || !stars.isEmpty()) {
                    relevant.add(new Seen(member.getIdLong(), vip, VIPRoleHandler.getNameColor(member), stars));
                }
            }).setTimeout(Duration.ofMinutes(5)).get();
        } catch (Exception e) {
            // Without every member, a star in use can't be told apart from an unused one.
            System.out.println("Could not load members, skipping VIP star update: " + e);
            return;
        }

        // The colors VIPs need, and the existing roles that serve them.
        Set<Integer> neededColors = new HashSet<>();
        for (Seen member : relevant) {
            if (member.vip() && member.color() != Role.DEFAULT_COLOR_RAW) {
                neededColors.add(member.color());
            }
        }

        Set<Long> keep = new HashSet<>();
        for (int color : neededColors) {
            Role role = VIPRoleHandler.getRole(guild, color);
            if (role != null) {
                keep.add(role.getIdLong());
            }
        }

        // Delete unused star roles first: that takes the star from anyone still holding one, and frees room for new roles.
        int deleted = 0;
        for (Role role : guild.getRoles()) {
            if (VIPRoleHandler.isStarRole(role) && !keep.contains(role.getIdLong())) {
                VIPRoleHandler.deleteRole(role);
                deleted++;
            }
        }

        int created = 0;
        Map<Integer, Role> roles = new HashMap<>();
        for (int color : neededColors) {
            Role role = VIPRoleHandler.getRole(guild, color);
            if (role == null) {
                role = VIPRoleHandler.createRole(guild, color);
                if (role != null) {
                    created++;
                }
            }
            if (role != null) {
                roles.put(color, role);
            }
        }

        for (Seen member : relevant) {
            UserSnowflake user = UserSnowflake.fromId(member.id());
            Role wanted = member.vip() ? roles.get(member.color()) : null;

            if (wanted != null && !member.stars().contains(wanted.getIdLong())) {
                guild.addRoleToMember(user, wanted)
                        .reason("User has VIP Pass")
                        .queue();
            }

            for (long roleId : member.stars()) {
                if (!keep.contains(roleId) || (wanted != null && roleId == wanted.getIdLong())) {
                    // Deleted above, or the one they should have.
                    continue;
                }

                Role role = guild.getRoleById(roleId);
                if (role != null) {
                    guild.removeRoleFromMember(user, role)
                            .reason(member.vip() ? "User's name color changed" : "User's VIP Pass has expired")
                            .queue();
                }
            }
        }

        if (deleted > 0 || created > 0) {
            System.out.printf("VIP stars: %s VIPs, %s colors, %s roles deleted, %s created%n", vipIds.size(), neededColors.size(), deleted, created);
        }
    }
}
