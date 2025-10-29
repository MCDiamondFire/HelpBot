package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.sys.message.acceptors.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageEvent extends ListenerAdapter {
    
    private static final MessageAcceptor[] acceptors = {
            new FilterAcceptor(),
            new CommandAcceptor(),
            new TagAcceptor(),
            new VerifyAcceptor()
    };
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        for (MessageAcceptor acceptor : acceptors) {
            if (acceptor.accept(message)) {
                break;
            }
        }
    }
}
