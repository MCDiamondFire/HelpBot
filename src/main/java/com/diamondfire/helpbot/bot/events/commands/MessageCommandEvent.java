package com.diamondfire.helpbot.bot.events.commands;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.command.reply.handler.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

public class MessageCommandEvent extends GuildMessageReceivedEvent implements CommandEvent {
    private Command command;
    private final MessageReplyHandler replyHandler = new MessageReplyHandler(getChannel());
    //TODO Cleanup and refactor this.
    // I'd like to see stuff like replying be put into it's whole own section and refactored as well.
    private ParsedArgumentSet parsedArgumentSet = null;
    private String aliasedUsed = null;
    
    public MessageCommandEvent(Message message) {
        super(message.getJDA(), 0, message);
        String[] rawArgs = getMessage().getContentDisplay().split(" ");
        String commandPrefix = rawArgs[0].substring(HelpBotInstance.getConfig().getPrefix().length()).toLowerCase();
        
        Command cmd = CommandHandler.getInstance().getCommands().get(commandPrefix.toLowerCase());
        if (cmd == null) {
            this.aliasedUsed = commandPrefix.toLowerCase();
            cmd = CommandHandler.getInstance().getAliases().get(commandPrefix.toLowerCase());
        }
        
        this.command = cmd;
    }
    
    @Override
    public void pushArguments(String[] rawArgs) throws ArgumentException  {
        this.parsedArgumentSet = ArgumentParser.parseArgs(command, Arrays.copyOfRange(rawArgs, 1, rawArgs.length), this);
    }
    
    @Override
    public Command getCommand() {
        return command;
    }
    
    @Override
    public void setCommand(Command command) {
        this.command = command;
    }
    
    @Override
    public void reply(PresetBuilder preset) {
        replyHandler.reply(preset, getChannel());
    }
    
    @Override
    public void replyEphemeral(PresetBuilder preset) {
        reply(preset);
    }
    
    @Override
    public <T> T getArgument(String code) {
        return parsedArgumentSet.getArgument(code);
    }
    
    @Override
    public Map<String, ?> getArguments() {
        return parsedArgumentSet.getArguments();
    }
    
    @Override
    public ReplyHandler getReplyHandler() {
        return replyHandler;
    }
    
    @Override
    public String getAliasUsed() {
        return aliasedUsed;
    }
    
    public String[] getRawArgs() {
        return getMessage().getContentRaw().split("\\s+");
    }
}
