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
        
        List<Member> members = guild.loadMembers().get();
        Set<Long> vipIds = VIPRoleHandler.retrieveVIPs();
        
        for (Member member : members) {
            Role role = VIPRoleHandler.getRole(member.getColorRaw());
            if (role == null) {
                continue;
            }
            if (vipIds.contains(member.getIdLong()) && !member.getRoles().contains(role)) {
                guild.addRoleToMember(member, role).queue();
                continue;
            }
            if (member.getRoles().contains(role)) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }
    }
    
}
