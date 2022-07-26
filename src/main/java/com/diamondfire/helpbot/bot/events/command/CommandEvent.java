package com.diamondfire.helpbot.bot.events.command;

import com.diamondfire.helpbot.bot.command.argument.ParseResults;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.reply.handler.ReplyHandler;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public abstract class CommandEvent {
    // we need this to be late init in downstream constructors
    protected Command baseCommand;
    protected ParseResults parseResults;
    protected final ReplyHandler replyHandler;


    protected CommandEvent(ReplyHandler replyHandler) {
        this.replyHandler = replyHandler;
    }


    public Command getBaseCommand() {
        return baseCommand;
    }

    @ApiStatus.Internal
    public void setBaseCommand(Command baseCommand) {
        this.baseCommand = baseCommand;
    }
    
    public void reply(PresetBuilder preset) {
        replyHandler.reply(preset);
    }

    public ReplyHandler getReplyHandler() {
        return replyHandler;
    }

    public abstract String getAliasUsed();

    public ParseResults getParseResults() {
        return parseResults;
    }

    public ParsedArgumentSet getArgumentSet() {
        return parseResults.parsedArgumentSet;
    }

    public Map<String, ?> getArguments() {
        return parseResults.parsedArgumentSet.getArguments();
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String code) {
        return (T) getArguments().get(code);
    }

    public void executeParse() throws ArgumentException {
        this.parseResults = parseCommand();
    }

    protected abstract ParseResults parseCommand() throws ArgumentException;

    public abstract JDA getJDA();

    public abstract Guild getGuild();

    public abstract Member getMember();

    public abstract User getAuthor();

    @ApiStatus.Internal
    public abstract TextChannel getChannel();
}
