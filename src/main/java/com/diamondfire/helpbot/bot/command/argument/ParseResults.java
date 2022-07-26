package com.diamondfire.helpbot.bot.command.result;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.result.ExecutionStack;

public class ParseResults {
    public final ExecutionStack executionStack;
    public final ParsedArgumentSet parsedArgumentSet;


    public ParseResults(ExecutionStack executionStack, ParsedArgumentSet parsedArgumentSet) {
        this.executionStack = executionStack;
        this.parsedArgumentSet = parsedArgumentSet;
    }
}
