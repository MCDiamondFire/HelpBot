package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.LoopingTask;
import com.diamondfire.helpbot.sys.vip.VIPRoleHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class VIPStarTask implements LoopingTask {

    private static final long VIP_PASS_HOLDER_ROLE = 0L;

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

        assert guild != null;

        List<Member> members = guild.loadMembers().get();
        Set<Long> vipIds = VIPRoleHandler.retrieveVIPs();
        Role generalRole = guild.getRoleById(VIP_PASS_HOLDER_ROLE);

        assert generalRole != null;

        for (Member member : members) {
            Role role = VIPRoleHandler.getOrCreateRole(member.getColorRaw());
            if (role == null) {
                continue;
            }
            if (vipIds.contains(member.getIdLong())) {
                if (!member.getRoles().contains(role)) {
                    guild.addRoleToMember(member, role).queue();
                }
                if (!member.getRoles().contains(generalRole)) {
                    guild.addRoleToMember(member, generalRole).queue();
                }
            } else {
                if (member.getRoles().contains(role)) {
                    guild.removeRoleFromMember(member, role).queue();
                }
                if (member.getRoles().contains(generalRole)) {
                    guild.removeRoleFromMember(member, generalRole).queue();
                }
            }
        }
    }

}
