package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

public class CommandEvent extends GuildMessageReceivedEvent {
    
    private final Command command;
    private final ReplyHandler replyHandler = new ReplyHandler(getChannel());
    //TODO Cleanup and refactor this. I'd like to see stuff like replying be put into it's whole own section and refactored as well.
    private ParsedArgumentSet parsedArgumentSet = null;
    private String aliasedUsed = null;
    
    public CommandEvent(Message message) {
        super(message.getJDA(), 0, message);
        String[] rawArgs = getMessage().getContentDisplay().split(" ");
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
        replyHandler.reply(preset, getChannel());
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
}
