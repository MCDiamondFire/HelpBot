package com.diamondfire.helpbot.df.codeinfo.viewables;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.*;
import net.dv8tion.jda.api.requests.RestAction;

public class BasicReaction {
    
    private final Emoji emoji;
    
    public BasicReaction(Emoji emoji) {
        this.emoji = emoji;
    }
    
    
    
    public String getUnicode() {
        if (emoji.getType() != Emoji.Type.UNICODE) {
            throw new IllegalStateException("Emoji is not a unicode char!");
        }
        
        return emoji.getName();
    }
    
    public CustomEmoji getEmote() {
        if (emoji.getType() != Emoji.Type.CUSTOM) {
            throw new IllegalStateException("Emoji is not a unicode char!");
        }
        
        return (CustomEmoji) emoji;
    }
    
    public RestAction<Void> react(Message message) {
        return message.addReaction(emoji);
    }
    
    @Override
    public String toString() {
        return this.emoji.getFormatted();
    }
}
