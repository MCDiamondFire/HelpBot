package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelArchiveEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateArchived(ChannelUpdateArchivedEvent event) {
        // Limit to help forum.
        if (
                event.getChannel().getType() != ChannelType.GUILD_PUBLIC_THREAD ||
                        event.getChannel().asThreadChannel().getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()
        ) {
            return;
        }
        
        // When a post is archived, it should be locked.
        event.getChannel().asThreadChannel().getManager().setLocked(true).queue();
    }
    
}
