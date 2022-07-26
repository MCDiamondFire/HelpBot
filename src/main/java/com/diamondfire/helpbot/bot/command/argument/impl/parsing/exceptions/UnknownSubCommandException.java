package com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions;

import com.diamondfire.helpbot.bot.command.argument.ParseResults;
import com.diamondfire.helpbot.bot.command.impl.SubCommandHolder;
import com.diamondfire.helpbot.util.FormatUtil;

public class UnknownSubCommandException extends ArgumentException {
    public final ParseResults.ExecutionStack executionStack;
    public final String subCommandName;

    public UnknownSubCommandException(ParseResults.ExecutionStack executionStack, String subCommandName) {
        super("Not a valid subcommand. Choose from " + executionStack.last().getHelpContext().getArguments().get(0).getArgumentName());
        this.executionStack = executionStack;
        this.subCommandName = subCommandName;
    }
}
