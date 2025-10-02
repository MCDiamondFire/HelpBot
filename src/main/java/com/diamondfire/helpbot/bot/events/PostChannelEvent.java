package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.util.SolvedPostManager;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

public class PostChannelEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateAppliedTags(ChannelUpdateAppliedTagsEvent event) {
        Optional<ThreadChannel> optionalChannel = SolvedPostManager.getHelpPost(event.getChannel());
        if (optionalChannel.isEmpty()) {
            return;
        }
        ThreadChannel threadChannel = optionalChannel.get();
        
        Optional<ForumTag> optionalTag = SolvedPostManager.getSolvedTag(threadChannel);
        if (optionalTag.isEmpty()) {
            return;
        }
        ForumTag solvedTag = optionalTag.get();
        
        if (event.getAddedTags().contains(solvedTag)) {
            SolvedPostManager.addSolved(threadChannel);
        } else if (event.getRemovedTags().contains(solvedTag)) {
            SolvedPostManager.removeSolved(threadChannel);
        }
    }
    
}
