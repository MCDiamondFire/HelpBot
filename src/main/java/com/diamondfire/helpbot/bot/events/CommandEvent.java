package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import java.util.*;

public class CommandEvent {
    
    private final Member member;
    private final TextChannel channel;
    
    private final Command command;
    private final ReplyHandler replyHandler;
    //TODO Cleanup and refactor this. I'd like to see stuff like replying be put into it's whole own section and refactored as well.
    private ParsedArgumentSet parsedArgumentSet = null;
    private String aliasedUsed = null;
    
    public CommandEvent(Member member, TextChannel channel, ReplyAction replyAction) {
        this.member = member;
        this.channel = channel;
        this.replyHandler  = new ReplyHandler(replyAction);
        
        String[] rawArgs = message.getContentDisplay().split(" ");
        String commandPrefix = rawArgs[0].substring(HelpBotInstance.getConfig().getPrefix().length()).toLowerCase();
        
        
        Command cmd = HelpBotInstance.getHandler().getCommands().get(commandPrefix.toLowerCase());
        if (cmd == null) {
            this.aliasedUsed = commandPrefix.toLowerCase();
            cmd = HelpBotInstance.getHandler().getAliases().get(commandPrefix.toLowerCase());
        }
        
        this.command = cmd;
    }
    
    public void pushArguments(String[] rawArgs) throws ArgumentException {
        this.parsedArgumentSet = ArgumentParser.parseArgs(command, Arrays.copyOfRange(rawArgs, 1, rawArgs.length));
    }
    
    public Command getCommand() {
        return command;
    }
    
    public void reply(PresetBuilder preset) {
        replyHandler.reply(preset);
    }
    
    public <T> T getArgument(String code) {
        return parsedArgumentSet.getArgument(code);
    }
    
    public Map<String, ?> getArguments() {
        return parsedArgumentSet.getArguments();
    }
    
    public ReplyHandler getReplyHandler() {
        return replyHandler;
    }
    
    public String getAliasUsed() {
        return aliasedUsed;
    }
    
    public Member getMember() {
        return member;
    }
    
    public TextChannel getChannel() {
        return channel;
    }
    
    public Guild getGuild() {
        return channel.getGuild();
    }
}
