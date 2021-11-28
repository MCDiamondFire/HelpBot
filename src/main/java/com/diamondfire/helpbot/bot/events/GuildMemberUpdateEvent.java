package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.tasks.impl.SupporterClassTask;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildMemberUpdateEvent extends ListenerAdapter {
    
    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        SupporterClassTask.updateMember(event.getMember());
    }
    
    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        SupporterClassTask.updateMember(event.getMember());
    }
}
