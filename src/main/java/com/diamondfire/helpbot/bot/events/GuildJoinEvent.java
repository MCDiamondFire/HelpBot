package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.impl.MuteExpireTask;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinEvent extends ListenerAdapter {
    
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() == HelpBotInstance.DF_GUILD) {
            Member member = event.getMember();
            Util.updateMember(member);
            MuteExpireTask.handle(member);
        }
    }
}
