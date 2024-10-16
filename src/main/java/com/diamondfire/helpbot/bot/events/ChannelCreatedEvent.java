package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
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
        var solvedTag = event.getChannel().asThreadChannel().getParentChannel().asForumChannel().getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag());
        if (event.getChannel().asThreadChannel().getAppliedTags().contains(solvedTag)) {
            var appliedTags = new ArrayList<>(event.getChannel().asThreadChannel().getAppliedTags());
            appliedTags.remove(solvedTag);
            event.getChannel().asThreadChannel().getManager().setAppliedTags(appliedTags).queue();
        }
        
    }
    
}
