package com.diamondfire.helpbot.bot.command.reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class ReplyHandler {

    public MessageAction embedReply(EmbedBuilder embed, MessageChannel channel) {
        return channel.sendMessage(embed.build());
    }

    public MessageAction textReply(String msg, MessageChannel channel) {
        return channel.sendMessage(msg);
    }

}
