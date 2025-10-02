package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PostChannelEvent extends ListenerAdapter {
    
    @Override
    public void onChannelUpdateAppliedTags(ChannelUpdateAppliedTagsEvent event) {
        // Limit to help forum.
        ChannelUnion channel = event.getChannel();
        ThreadChannel threadChannel = channel.asThreadChannel();
        if (threadChannel.getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()) {
            return;
        }
        
        ForumTag solvedTag = threadChannel.getParentChannel().asForumChannel().getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag());
        
        // If the solved tag is added, add [SOLVED] to the thread's name.
        if (event.getAddedTags().contains(solvedTag) && !threadChannel.getName().startsWith("[SOLVED] ")) {
            sendSolvedEmbed(threadChannel, "Post marked as solved");
            threadChannel.getManager().setName("[SOLVED] " + channel.getName()).queue();
        } else if (event.getRemovedTags().contains(solvedTag) && threadChannel.getName().startsWith("[SOLVED] ")) {
            sendSolvedEmbed(threadChannel, "Post marked as unsolved");
            String newName = threadChannel.getName().replaceFirst("\\[SOLVED] ", "");
            if (newName.isEmpty()) {
                newName = "Post";
            }
            threadChannel.getManager().setName(newName).queue();
        }
    }
    
    public static void sendSolvedEmbed(ThreadChannel threadChannel, String message) {
        threadChannel.sendMessageEmbeds(
                new PresetBuilder()
                        .withPreset(
                                new InformativeReply(InformativeReplyType.SUCCESS, message)
                        ).getEmbed().build()
        ).queue();
    }
    
}
