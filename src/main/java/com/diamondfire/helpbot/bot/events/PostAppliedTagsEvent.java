package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateAppliedTagsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PostAppliedTagsEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateAppliedTags(ChannelUpdateAppliedTagsEvent event) {
        // Limit to help forum.
        if (event.getChannel().asThreadChannel().getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()) {
            return;
        }
        
        var solvedTag = event.getChannel().asThreadChannel().getParentChannel().asForumChannel().getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag());
        
        // If the solved tag is added and the post is not locked, lock the thread.
        if (event.getAddedTags().contains(solvedTag) && !event.getChannel().asThreadChannel().isLocked()) {
            event.getChannel().asThreadChannel().getManager().setLocked(true).queue();
            ThreadChannel threadChannel = HelpBotInstance.getJda().getThreadChannelById(event.getChannel().getIdLong());
            if (threadChannel != null) {
                threadChannel.sendMessageEmbeds(
                        new PresetBuilder()
                                .withPreset(
                                        new InformativeReply(InformativeReplyType.SUCCESS, "Post marked as solved")
                                ).getEmbed().build()
                ).queue();
            }
            event.getChannel().asThreadChannel().getManager().setName("[SOLVED] " + event.getChannel().getName()).queue();
        } else if (event.getRemovedTags().contains(solvedTag) && event.getChannel().asThreadChannel().isLocked()) {
            // If the solved tag is removed and the post is locked, put the old tags back.
            event.getChannel().asThreadChannel().getManager().setAppliedTags(event.getOldTags()).queue();
        }
    }
    
}
