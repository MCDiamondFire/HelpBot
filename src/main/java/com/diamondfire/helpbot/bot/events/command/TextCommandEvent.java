package com.diamondfire.helpbot.bot.events.command;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.UnknownSubCommandException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.impl.SubCommandHolder;
import com.diamondfire.helpbot.bot.command.reply.handler.*;
import com.diamondfire.helpbot.bot.command.argument.ParseResults;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class TextCommandEvent extends CommandEvent {
    protected final TextChannel channel;
    protected final Member member;
    protected final String[] rawArguments;

    //TODO Cleanup and refactor this.
    // I'd like to see stuff like replying be put into it's whole own section and refactored as well.
    protected ParsedArgumentSet parsedArgumentSet = null;
    protected final String aliasUsed;

    public TextCommandEvent(TextChannel channel, Member member, String[] rawArguments) {
        super(new MessageReplyHandler(channel));

        this.channel = channel;
        this.member = member;

        this.rawArguments = rawArguments;
        this.aliasUsed = rawArguments[0].substring(HelpBotInstance.getConfig().getPrefix().length()).toLowerCase();

        this.baseCommand = CommandHandler.getInstance().getCommands().get(aliasUsed);
        if (baseCommand == null) {
            this.baseCommand = CommandHandler.getInstance().getAliases().get(aliasUsed);
        }
    }

    @Override
    public ParseResults parseCommand() throws ArgumentException {
        String[] rawArgs = Arrays.copyOfRange(rawArguments, 1, rawArguments.length);
        Deque<String> arguments = new ArrayDeque<>(Arrays.asList(rawArgs));

        Command current = this.baseCommand;
        ParseResults.ExecutionStack executionStack = new ParseResults.ExecutionStack();
        executionStack.getAsList().add(current);

        while (current instanceof SubCommandHolder subCommandHolder) {
            String subCommandName = arguments.peek();

            if (subCommandName == null) subCommandName = subCommandHolder.getDefaultSubCommand();

            current = subCommandName == null ? null : subCommandHolder.getSubCommand(subCommandName);

            if (current == null) throw new UnknownSubCommandException(executionStack, subCommandName);
            else executionStack.getAsList().add(current);
        }

        ParsedArgumentSet parsedArgumentSet = ArgumentParser.parseArgs(this, executionStack, arguments);

        return new ParseResults(executionStack, parsedArgumentSet);
    }

    @Override
    public String getAliasUsed() {
        return aliasUsed;
    }

    @Override
    public JDA getJDA() {
        return channel.getJDA();
    }

    @Override
    public Guild getGuild() {
        return channel.getGuild();
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public User getAuthor() {
        return member.getUser();
    }

    @Override
    public TextChannel getChannel() {
        return channel;
    }

    @ApiStatus.Internal
    public String[] getRawArgs() {
        return rawArguments;
    }
}
