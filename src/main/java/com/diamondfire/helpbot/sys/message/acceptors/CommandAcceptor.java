package com.diamondfire.helpbot.sys.message.acceptors;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.entities.Message;

public class CommandAcceptor implements MessageAcceptor {
    
    @Override
    public boolean accept(Message message) {
        if (message.getContentDisplay().startsWith(HelpBotInstance.getConfig().getPrefix()) && !message.getAuthor().isBot()) {
            HelpBotInstance.getHandler().run(new CommandEvent(message));
            return true;
        }
        
        return false;
    }
}
