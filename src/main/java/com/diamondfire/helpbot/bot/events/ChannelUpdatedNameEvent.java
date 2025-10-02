package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.util.SolvedPostManager;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.*;

import java.util.Optional;

public class ChannelUpdatedNameEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        Optional<ThreadChannel> optionalChannel = SolvedPostManager.getHelpPost(event.getChannel());
        if (optionalChannel.isEmpty()) {
            return;
        }
        ThreadChannel threadChannel = optionalChannel.get();
        
        Optional<ForumTag> optionalTag = SolvedPostManager.getSolvedTag(threadChannel);
        if (optionalTag.isEmpty() || event.getOldValue() == null || event.getNewValue() == null) {
            return;
        }
        ForumTag solvedTag = optionalTag.get();
        
        if (event.getOldValue().startsWith("[SOLVED] ") && !event.getNewValue().startsWith("[SOLVED] ") &&
                threadChannel.getAppliedTags().contains(solvedTag)) {
            
            SolvedPostManager.removeSolved(threadChannel);
        }
        else if (!event.getOldValue().startsWith("[SOLVED] ") && event.getNewValue().startsWith("[SOLVED] ") &&
                !threadChannel.getAppliedTags().contains(solvedTag)) {
            
            SolvedPostManager.addSolved(threadChannel);
        }
    }
    
}
