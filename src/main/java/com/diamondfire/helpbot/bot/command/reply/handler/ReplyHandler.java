package com.diamondfire.helpbot.bot.command.reply.handler;

import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public interface ReplyHandler {
    void reply(String content);
    void reply(PresetBuilder preset);
    void reply(PresetBuilder preset, MessageChannel channel);
    void reply(EmbedBuilder builder);
    void reply(EmbedBuilder builder, MessageChannel channel);
    
    MessageAction replyA(PresetBuilder preset);
    MessageAction replyA(PresetBuilder preset, MessageChannel channel);
    MessageAction embedReply(EmbedBuilder embed, MessageChannel channel);
    MessageAction textReply(String msg, MessageChannel channel);
    
}
