package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.message.acceptors.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


import javax.annotation.Nonnull;

public class MessageEditEvent extends ListenerAdapter {
    
    private static final MessageAcceptor[] acceptors = {
            new FilterAcceptor(),
    };
    
    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        Message message = event.getMessage();
        for (MessageAcceptor acceptor : acceptors) {
            if (acceptor.accept(message)) {
                break;
            }
        }
        
    }
    
}
