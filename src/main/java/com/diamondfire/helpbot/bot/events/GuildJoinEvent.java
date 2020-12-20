package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.rolereact.RoleReactListener;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class GuildJoinEvent extends ListenerAdapter {
    
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() == HelpBotInstance.DF_GUILD) {
            Member member = event.getMember();
            Util.updateMember(member);
            RoleReactListener.refreshRoles(member);
        }
    }
}
