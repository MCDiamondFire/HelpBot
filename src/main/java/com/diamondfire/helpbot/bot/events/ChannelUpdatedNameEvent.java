package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
        
        if (solvedTag == null || event.getOldValue() == null || event.getNewValue() == null) return;
        
        // If the post starts with [SOLVED] and has a solved tag, but renamed to not have [SOLVED], then unsolve.
        if (event.getOldValue().startsWith("[SOLVED] ") && !event.getNewValue().startsWith("[SOLVED] ") && threadChannel.getAppliedTags().contains(solvedTag)) {
            ArrayList<ForumTag> appliedTags = new ArrayList<>(threadChannel.getAppliedTags());
            appliedTags.remove(solvedTag);
            threadChannel.getManager().setAppliedTags(appliedTags).queue();
            PostChannelEvent.sendSolvedEmbed(threadChannel, "Post marked as unsolved");
            // If the post does not start with [SOLVED] and does not have a solved tag, but renamed to have [SOLVED], then solve.
        } else if (!event.getOldValue().startsWith("[SOLVED] ") && event.getNewValue().startsWith("[SOLVED] ") && !threadChannel.getAppliedTags().contains(solvedTag)) {
            ArrayList<ForumTag> appliedTags = new ArrayList<>(threadChannel.getAppliedTags());
            appliedTags.add(solvedTag);
            threadChannel.getManager().setAppliedTags(appliedTags).setAppliedTags(solvedTag).queue();
            PostChannelEvent.sendSolvedEmbed(threadChannel, "Post marked as solved");
        }
        
    }
    
}
