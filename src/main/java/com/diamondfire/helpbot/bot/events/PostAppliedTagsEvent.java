package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateAppliedTagsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PostAppliedTagsEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateAppliedTags(ChannelUpdateAppliedTagsEvent event) {
        // Limit to help forum.
        ChannelUnion channel = event.getChannel();
        ThreadChannel threadChannel = channel.asThreadChannel();
        if (threadChannel.getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()) {
            return;
        }
        
        ForumTag solvedTag = threadChannel.getParentChannel().asForumChannel().getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag());
        
        // If the solved tag is added and the post is not locked, lock the thread.
        if (event.getAddedTags().contains(solvedTag) && !threadChannel.isLocked()) {
            threadChannel.getManager().setLocked(true).queue();
            threadChannel.sendMessageEmbeds(
                    new PresetBuilder()
                            .withPreset(
                                    new InformativeReply(InformativeReplyType.SUCCESS, "Post marked as solved")
                            ).getEmbed().build()
            ).queue();
            threadChannel.getManager().setName("[SOLVED] " + channel.getName()).queue();
        } else if (event.getRemovedTags().contains(solvedTag) && threadChannel.isLocked()) {
            // If the solved tag is removed and the post is locked, put the old tags back.
            threadChannel.getManager().setAppliedTags(event.getOldTags()).queue();
        }
    }
    
}
