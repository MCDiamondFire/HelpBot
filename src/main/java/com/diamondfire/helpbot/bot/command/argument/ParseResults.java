package com.diamondfire.helpbot.bot.command.argument;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.impl.Command;

import java.util.ArrayList;
import java.util.List;

public class ParseResults {
    public final ExecutionStack executionStack;
    public final ParsedArgumentSet parsedArgumentSet;


    public ParseResults(ExecutionStack executionStack, ParsedArgumentSet parsedArgumentSet) {
        this.executionStack = executionStack;
        this.parsedArgumentSet = parsedArgumentSet;
    }

    public static class ExecutionStack {
        private final List<Command> commandList;

        public ExecutionStack() {
            this(new ArrayList<>());
        }

        public ExecutionStack(List<Command> commandList) {
            this.commandList = commandList;
        }

        public Command last() {
            return commandList.get(commandList.size() - 1);
        }

        public List<Command> getAsList() {
            return commandList;
        }
    }
}
