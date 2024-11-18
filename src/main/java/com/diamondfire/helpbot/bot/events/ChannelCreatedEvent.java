package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class ChannelCreatedEvent extends ListenerAdapter {
    
    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        // Limit to help forum.
        if (
                event.getChannel().getType() != ChannelType.GUILD_PUBLIC_THREAD ||
                        event.getChannel().asThreadChannel().getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()
        ) {
            return;
        }
        
        // Remove solved tag if post was created with it.
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();
        
        ForumTag solvedTag = threadChannel.getParentChannel().asForumChannel().getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag());
        if (threadChannel.getAppliedTags().contains(solvedTag)) {
            ArrayList<ForumTag> appliedTags = new ArrayList<>(threadChannel.getAppliedTags());
            // Solved tag is the only tag, and we need at least one tag.
            // In this case, this will do nothing, however ?solved will still change post's the name, so it's fine.
            if (appliedTags.size() != 1) {
                appliedTags.remove(solvedTag);
            }
            threadChannel.getManager().setAppliedTags(appliedTags).queue();
        }
        
    }
    
}
