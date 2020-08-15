package com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions;

import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.util.StringUtil;

public abstract class ArgumentException extends Exception {

    private String message;

    public ArgumentException(String message) {
        super(message);
        this.message = message;
    }

    public String getEmbedMessage() {
        return message;
    }

    public void setContext(Command command, int pos) {
        String[] args = StringUtil.getArgumentDisplay(command.getHelpContext());
        args[pos] = "**" + args[pos] + "**";
        String argMessage = StringUtil.displayCommand(command) + " " + String.join(" ", args);

        message = argMessage + "\n\n" + getMessage();
    }

}
