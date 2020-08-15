package com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ArgumentNode;
import com.diamondfire.helpbot.bot.command.impl.Command;

public class ArgumentExceptionContext {

    private final Command cmd;
    private final ArgumentNode<?> currentNode;
    private final int position;

    public ArgumentExceptionContext(Command set, ArgumentNode<?> currentNode, int position) {
        this.cmd = set;
        this.currentNode = currentNode;
        this.position = position;
    }

    public Command getCmd() {
        return cmd;
    }

    public ArgumentNode<?> getCurrentNode() {
        return currentNode;
    }

    public int getPosition() {
        return position;
    }
}
