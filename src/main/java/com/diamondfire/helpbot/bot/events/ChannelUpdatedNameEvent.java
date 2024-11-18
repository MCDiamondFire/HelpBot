package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChannelUpdatedNameEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        // Limit to help forum.
        if (
                event.getChannel().getType() != ChannelType.GUILD_PUBLIC_THREAD ||
                        event.getChannel().asThreadChannel().getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()
        ) {
            return;
        }
        
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();
        ForumTag solvedTag = threadChannel.getParentChannel().asForumChannel().getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag());
        
        if (event.getOldValue() == null || event.getNewValue() == null) return;
        
        // If the post starts with [SOLVED] and has a solved tag, but renamed to not have [SOLVED], then revert to the old name.
        if (event.getOldValue().startsWith("[SOLVED] ") && !event.getNewValue().startsWith("[SOLVED] ") && threadChannel.getAppliedTags().contains(solvedTag)) {
            threadChannel.getManager().setName(event.getOldValue()).queue();
        // If the post does not start with [SOLVED] and does not have a solved tag, but renamed to have [SOLVED], then revert to the old name.
        } else if (!event.getOldValue().startsWith("[SOLVED] ") && event.getNewValue().startsWith("[SOLVED] ") && !threadChannel.getAppliedTags().contains(solvedTag)) {
            threadChannel.getManager().setName(event.getOldValue()).queue();
        }
        
    }
    
}
