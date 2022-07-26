package com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.ParseResults;
import com.diamondfire.helpbot.bot.command.impl.*;
import com.diamondfire.helpbot.bot.events.command.*;
import com.diamondfire.helpbot.util.FormatUtil;

import java.util.List;

public class ArgumentException extends Exception {
    
    protected String message;
    
    public ArgumentException(String message) {
        super(message);
        this.message = message;
    }
    
    public String getEmbedMessage() {
        return message;
    }
    
    public void setContext(ParseResults.ExecutionStack executionStack, int pos, CommandEvent event) {
        String prefix = HelpBotInstance.getConfig().getPrefix();

        Command command = executionStack.last();

        String[] args = FormatUtil.getArgumentDisplay(command.getHelpContext());
        args[pos] = "**" + args[pos] + "**";
        String argMessage = prefix + FormatUtil.displayExecutionStack(executionStack) + " " + String.join(" ", args);
        
        message = argMessage + "\n\n" + getMessage();
    }
    
}
