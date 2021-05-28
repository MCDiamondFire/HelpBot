package com.diamondfire.helpbot.bot.command.reply;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

public class ReplyHandler {
    
    private final ReplyAction action;
    
    public ReplyHandler(ReplyAction action) {
        this.action = action;
    }
    
    public void reply(String content) {
        textReply(content).queue();
    }
    
    public void reply(PresetBuilder preset) {
        reply(preset);
    }
    
    public void reply(EmbedBuilder builder) {
        embedReply(builder).queue();
    }
    
    public ReplyAction replyA(PresetBuilder preset) {
        return embedReply(preset.getEmbed());
    }
    
    public ReplyAction embedReply(EmbedBuilder embed) {
        return action.addEmbeds(embed.build());
    }
    
    public ReplyAction textReply(String msg) {
        return action.setContent(msg);
    }
    
}
