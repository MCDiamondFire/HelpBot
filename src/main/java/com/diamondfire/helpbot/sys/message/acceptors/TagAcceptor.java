package com.diamondfire.helpbot.sys.message.acceptors;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tag.*;
import com.diamondfire.helpbot.sys.tag.exceptions.TagDoesNotExistException;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;

public class TagAcceptor implements MessageAcceptor {
    
    @Override
    public boolean accept(Message message) {
        if (message.getContentDisplay().startsWith(HelpBotInstance.getConfig().getPrefix()) &&
                !message.getAuthor().isBot()) {
            
            String parsedText = message.getContentStripped()
                    .substring(HelpBotInstance.getConfig().getPrefix().length())
                    .replaceFirst(" .*$", "");
            
            try {
                // Get Tag and send response
                TagHandler.getTag(parsedText)
                        .sendResponse(message.getChannel().asTextChannel(), message.getAuthor());
                
            } catch (TagDoesNotExistException | IOException ignored) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }
}
