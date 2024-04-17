package com.diamondfire.helpbot.bot.command.reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

public class ReplyHandler {
    
    private final TextChannel channel;
    
    public ReplyHandler(TextChannel channel) {
        this.channel = channel;
    }
    
    public TextChannel getChannel() {
        return channel;
    }
    
    public void reply(String content) {
        textReply(content, getChannel()).queue();
    }
    
    public void reply(PresetBuilder preset) {
        reply(preset, getChannel());
    }
    
    public void reply(PresetBuilder preset, MessageChannel channel) {
        reply(preset.getEmbed(), channel);
    }
    
    public void reply(EmbedBuilder builder) {
        embedReply(builder, getChannel()).queue();
    }
    
    public void reply(EmbedBuilder builder, MessageChannel channel) {
        embedReply(builder, channel).queue();
    }
    
    public MessageCreateAction replyA(PresetBuilder preset) {
        return replyA(preset, getChannel());
    }
    
    public MessageCreateAction replyA(PresetBuilder preset, MessageChannel channel) {
        return embedReply(preset.getEmbed(), channel);
    }
    
    public MessageCreateAction embedReply(EmbedBuilder embed, MessageChannel channel) {
        return channel.sendMessageEmbeds(embed.build());
    }
    
    public MessageCreateAction textReply(String msg, MessageChannel channel) {
        return channel.sendMessage(msg);
    }
    
}
