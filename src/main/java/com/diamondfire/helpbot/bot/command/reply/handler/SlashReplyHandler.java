package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class SlashReplyHandler implements ReplyHandler {
    
    @Override
    public void reply(String content) {}
    
    @Override
    public void reply(PresetBuilder preset) {}
    
    @Override
    public void reply(PresetBuilder preset, MessageChannel channel) {}
    
    @Override
    public void reply(EmbedBuilder builder) {}
    
    @Override
    public void reply(EmbedBuilder builder, MessageChannel channel) {}
    
    @Override
    public MessageAction replyA(PresetBuilder preset) {
        return null;
    }
    
    @Override
    public MessageAction replyA(PresetBuilder preset, MessageChannel channel) {
        return null;
    }
    
    @Override
    public MessageAction embedReply(EmbedBuilder embed, MessageChannel channel) {
        return null;
    }
    
    @Override
    public MessageAction textReply(String msg, MessageChannel channel) {
        return null;
    }
}
