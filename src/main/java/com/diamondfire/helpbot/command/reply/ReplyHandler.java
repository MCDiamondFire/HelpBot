package com.diamondfire.helpbot.command.reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class ReplyHandler {

    public MessageAction reply(EmbedBuilder embed, TextChannel channel) {
        return channel.sendMessage(embed.build());
    }

}
