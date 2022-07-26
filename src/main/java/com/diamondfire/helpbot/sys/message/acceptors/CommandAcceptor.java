package com.diamondfire.helpbot.sys.message.acceptors;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.events.command.*;
import net.dv8tion.jda.api.entities.Message;

public class CommandAcceptor implements MessageAcceptor {
    
    @Override
    public boolean accept(Message message) {
        if (message.getContentDisplay().startsWith(HelpBotInstance.getConfig().getPrefix()) && !message.getAuthor().isBot()) {
            MessageCommandEvent event = new MessageCommandEvent(message);
            if (event.getBaseCommand() == null) {
                return false;
            }
            
            CommandHandler.getInstance().run(event);
            return true;
        }
        
        return false;
    }
}
