package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.LoopingTask;
import com.diamondfire.helpbot.sys.vip.VIPRoleHandler;
import com.diamondfire.helpbot.util.MemberLoadGate;
import net.dv8tion.jda.api.entities.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Gives every VIP the star role for their name color, takes stars from everyone else,
 * and deletes star roles that no VIP needs.
 * <p>
 * It loads only the VIPs (by ID) and uses role member counts to tell whether anyone else holds a star,
 * instead of loading the whole member list, which Discord limits to once per 30 seconds.
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

        List<Member> vips;
        try {
            // VIPs who left the server are simply missing from the result.
            vips = guild.retrieveMembersByIds(vipIds).setTimeout(Duration.ofMinutes(1)).get();
        } catch (Exception e) {
            System.out.println("Could not load VIP members, skipping VIP star update: " + e);
            return;
        }

        // The star role each VIP should have, by name color.
        Map<Integer, Role> roles = new HashMap<>();
        for (Member vip : vips) {
            int color = VIPRoleHandler.getNameColor(vip);
            if (color != Role.DEFAULT_COLOR_RAW && !roles.containsKey(color)) {
                Role role = VIPRoleHandler.getRole(guild, color);
                if (role != null) {
                    roles.put(color, role);
                }
            }
        }
        Set<Long> keep = new HashSet<>();
        roles.values().forEach((role) -> keep.add(role.getIdLong()));

        // Deleting a role takes it from everyone, so unused stars need no member list. It also frees room for new roles.
        int deleted = 0;
        for (Role role : guild.getRoles()) {
            if (VIPRoleHandler.isStarRole(role) && !keep.contains(role.getIdLong())) {
                VIPRoleHandler.deleteRole(role);
                deleted++;
            }
        }

        int created = 0;
        for (Member vip : vips) {
            int color = VIPRoleHandler.getNameColor(vip);
            if (color != Role.DEFAULT_COLOR_RAW && !roles.containsKey(color)) {
                Role role = VIPRoleHandler.createRole(guild, color);
                if (role != null) {
                    roles.put(color, role);
                    keep.add(role.getIdLong());
                    created++;
                }
            }
        }

        // Give each VIP their star and take any other star they hold.
        Map<Long, Integer> heldByVips = new HashMap<>();
        for (Member vip : vips) {
            Role wanted = roles.get(VIPRoleHandler.getNameColor(vip));
            if (wanted != null) {
                heldByVips.merge(wanted.getIdLong(), 1, Integer::sum);
                if (!vip.getRoles().contains(wanted)) {
                    guild.addRoleToMember(vip, wanted)
                            .reason("User has VIP Pass")
                            .queue();
                }
            }

            for (Role role : vip.getRoles()) {
                if (keep.contains(role.getIdLong()) && !role.equals(wanted)) {
                    guild.removeRoleFromMember(vip, role)
                            .reason("User's name color changed")
                            .queue();
                }
            }
        }

        int removed = removeFromNonVips(guild, vipIds, keep, heldByVips);

        if (deleted > 0 || created > 0 || removed > 0) {
            System.out.printf("VIP stars: %s VIPs in server, %s colors, %s roles deleted, %s created, %s stars taken from non-VIPs%n",
                    vips.size(), roles.size(), deleted, created, removed);
        }
    }

    /**
     * Takes kept stars from members who aren't VIPs. Role member counts show whether anyone besides the VIPs
     * holds a star; only then are that role's holders loaded.
     */
    private static int removeFromNonVips(Guild guild, Set<Long> vipIds, Set<Long> keep, Map<Long, Integer> heldByVips) {
        RoleMemberCounts counts;
        try {
            counts = guild.retrieveRoleMemberCounts().complete();
        } catch (Exception e) {
            System.out.println("Could not get role member counts: " + e);
            return 0;
        }

        List<Role> extra = new ArrayList<>();
        for (long roleId : keep) {
            Role role = guild.getRoleById(roleId);
            // A count above the VIPs who hold the role means someone else has it too.
            if (role != null && counts.get(role) > heldByVips.getOrDefault(roleId, 0)) {
                extra.add(role);
            }
        }
        if (extra.isEmpty()) {
            return 0;
        }

        List<Member> holders;
        try {
            MemberLoadGate.await(guild);
            // findMembersWithRoles needs every role at once; any one of them is enough here.
            holders = guild.findMembers((member) -> member.getRoles().stream().anyMatch(extra::contains))
                    .setTimeout(Duration.ofMinutes(1)).get();
        } catch (Exception e) {
            System.out.println("Could not load star holders: " + e);
            return 0;
        }

        int removed = 0;
        for (Member member : holders) {
            if (vipIds.contains(member.getIdLong())) {
                continue;
            }

            for (Role role : member.getRoles()) {
                if (keep.contains(role.getIdLong())) {
                    guild.removeRoleFromMember(member, role)
                            .reason("User's VIP Pass has expired")
                            .queue();
                    removed++;
                }
            }
        }
        return removed;
    }
}
