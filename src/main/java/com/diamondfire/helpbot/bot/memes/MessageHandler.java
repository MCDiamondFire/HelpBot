package com.diamondfire.helpbot.bot.memes;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageHandler extends ListenerAdapter {
    
    private static final long MEMES_CHANNEL = HelpBotInstance.getConfig().getMemesChannel();
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        
        Message message = event.getMessage();
        
        if (event.getChannel().getIdLong() == MEMES_CHANNEL) {
            
            if (message.getAttachments().size() != 0) {
                
                message.addReaction(event.getGuild().getEmotesByName("upvote", true).get(0)).queue();
                message.addReaction(event.getGuild().getEmotesByName("downvote", true).get(0)).queue();
                
            } else {
    
                if (!message.getAuthor().isBot()) {
                    message.delete().queue();
                }
            }
        }
    }
}