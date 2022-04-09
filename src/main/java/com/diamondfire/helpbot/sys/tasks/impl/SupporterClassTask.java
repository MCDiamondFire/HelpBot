package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.LoopingTask;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SupporterClassTask implements LoopingTask {
    
    private static final long DISCORD_BOOSTER = 585528036070522894L;
    private static final long TWITCH_BOOSTER = 770098018510176256L;
    private static final long GENERAL_BOOST = 770103976506294303L;
    
    @Override
    public long getInitialStart() {
        return 0;
    }
    
    @Override
    public long getNextLoop() {
        return TimeUnit.HOURS.toMillis(1);
    }
    
    @Override
    public void run() {
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
        guild.loadMembers(SupporterClassTask::updateMember);
    }
    
    public static void updateMember(Member member) {
        Guild guild = member.getGuild();
        Role discord = guild.getRoleById(DISCORD_BOOSTER);
        Role twitch = guild.getRoleById(TWITCH_BOOSTER);
        Role generalBoost = guild.getRoleById(GENERAL_BOOST);
    
        List<Role> roles = member.getRoles();
        if ((roles.contains(discord) || roles.contains(twitch)) && !roles.contains(generalBoost)) {
            guild.addRoleToMember(member, generalBoost).queue();
        } else if (roles.contains(generalBoost) && !(roles.contains(discord) || roles.contains(twitch))) {
            guild.removeRoleFromMember(member, generalBoost).queue();
        }
    }
}
