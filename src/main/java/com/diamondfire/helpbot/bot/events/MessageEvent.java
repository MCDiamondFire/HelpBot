package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.message.acceptors.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MessageEvent extends ListenerAdapter {
    
    private static final MessageAcceptor[] acceptors = new MessageAcceptor[]{/*new FilterAcceptor(), */new CommandAcceptor()};
    
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        for (MessageAcceptor acceptor : acceptors) {
            if (!acceptor.accept(message)) {
                break;
            }
        }
        
    }
    
}
