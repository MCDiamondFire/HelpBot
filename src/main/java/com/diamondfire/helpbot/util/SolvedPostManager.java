package com.diamondfire.helpbot.util;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;

import java.util.*;

public final class SolvedPostManager {
    
    private static final int CHANNEL_NAME_LIMIT = 100;
    private static final String ELLIPSIS = "...";
    
    private SolvedPostManager() {}
    
    public static Optional<ForumTag> getSolvedTag(ThreadChannel channel) {
        return Optional.ofNullable(channel.getParentChannel()
                .asForumChannel()
                .getAvailableTagById(HelpBotInstance.getConfig().getHelpChannelSolvedTag()));
    }
    
    public static boolean isSolved(ThreadChannel channel) {
        return channel.getName().startsWith("[SOLVED] ");
    }
    
    public static void addSolved(ThreadChannel channel) {
        if (!applySolvedTag(channel)) return;
        
        if (!isSolved(channel)) {
            String newName = "[SOLVED] " + channel.getName();
            if (newName.length() > CHANNEL_NAME_LIMIT) {
                newName = newName.substring(0, CHANNEL_NAME_LIMIT - ELLIPSIS.length()) + ELLIPSIS;
            }
            channel.getManager().setName(newName).queue();
        }
        
        sendEmbed(channel, "Post marked as solved");
    }
    
    /**
     * @return true if successful
     */
    public static boolean applySolvedTag(ThreadChannel channel) {
        Optional<ForumTag> optionalTag = getSolvedTag(channel);
        if (optionalTag.isEmpty()) return false;
        ForumTag solvedTag = optionalTag.get();
        
        List<ForumTag> tags = new ArrayList<>(channel.getAppliedTags());
        if (!tags.contains(solvedTag)) {
            tags.add(solvedTag);
            channel.getManager().setAppliedTags(tags).queue();
        }
        return true;
    }
    
    public static void removeSolved(ThreadChannel channel) {
        Optional<ForumTag> optionalTag = getSolvedTag(channel);
        if (optionalTag.isEmpty()) return;
        ForumTag solvedTag = optionalTag.get();
        
        List<ForumTag> tags = new ArrayList<>(channel.getAppliedTags());
        if (tags.remove(solvedTag)) {
            channel.getManager().setAppliedTags(tags).queue();
        }
        
        if (isSolved(channel)) {
            String newName = channel.getName().replaceFirst("\\[SOLVED] ", "");
            channel.getManager().setName(newName.isEmpty() ? "Post" : newName).queue();
        }
        
        sendEmbed(channel, "Post marked as unsolved");
    }
    
    private static void sendEmbed(ThreadChannel channel, String message) {
        channel.sendMessageEmbeds(
                new PresetBuilder()
                        .withPreset(new InformativeReply(InformativeReplyType.SUCCESS, message))
                        .getEmbed()
                        .build()
        ).queue();
    }
    
    public static Optional<ThreadChannel> getHelpPost(ChannelUnion channel) {
        if (channel.getType() != ChannelType.GUILD_PUBLIC_THREAD) {
            return Optional.empty();
        }
        
        ThreadChannel threadChannel = channel.asThreadChannel();
        if (threadChannel.getParentChannel().getIdLong() != HelpBotInstance.getConfig().getHelpChannel()) {
            return Optional.empty();
        }
        
        return Optional.of(threadChannel);
    }
    
}