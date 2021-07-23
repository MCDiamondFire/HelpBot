package com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.FormatUtil;

public class ArgumentException extends Exception {
    
    private String message;
    
    public ArgumentException(String message) {
        super(message);
        this.message = message;
    }
    
    public String getEmbedMessage() {
        return message;
    }
    
    public void setContext(Command command, int pos, CommandEvent event) {
        String[] args = FormatUtil.getArgumentDisplay(command.getHelpContext());
        args[pos] = "**" + args[pos] + "**";
        String argMessage = FormatUtil.displayCommand(command) + " " + String.join(" ", args);
        
        if (command instanceof SubCommand) {
            String prefix = HelpBotInstance.getConfig().getPrefix();
            
            argMessage = event.getRawArgs()[0] + " " + argMessage.substring(prefix.length());
        }
        
        message = argMessage + "\n\n" + getMessage();
    }
    
}
