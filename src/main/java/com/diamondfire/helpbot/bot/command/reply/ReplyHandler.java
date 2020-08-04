package com.diamondfire.helpbot.bot.command.reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class ReplyHandler {

    public MessageAction reply(EmbedBuilder embed, MessageChannel channel) {
        return channel.sendMessage(embed.build());
    }

}
