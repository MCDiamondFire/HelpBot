package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.*;

public class CommandEvent extends GuildMessageReceivedEvent {

    private final Command command;
    private final ReplyHandler reply = new ReplyHandler();
    //TODO Cleanup and refactor this. I'd like to see stuff like replying be put into it's whole own section and refactored as well.
    private ParsedArgumentSet parsedArgumentSet = null;
    private String aliasedUsed = null;

    public CommandEvent(JDA api, long responseNumber, Message message) {
        super(api, responseNumber, message);
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

    // Soon, probs should clean this up.
    public void reply(String content) {
        reply.textReply(content, getChannel()).queue();
    }

    public void reply(PresetBuilder preset) {
        reply.embedReply(preset.getEmbed(), getChannel()).queue();
    }

    public void reply(PresetBuilder preset, MessageChannel channel) {
        reply.embedReply(preset.getEmbed(), channel).queue();
    }

    public void reply(EmbedBuilder builder) {
        reply.embedReply(builder, getChannel()).queue();
    }

    public void reply(EmbedBuilder builder, MessageChannel channel) {
        reply.embedReply(builder, channel).queue();
    }

    public MessageAction replyA(PresetBuilder preset) {
        return reply.embedReply(preset.getEmbed(), getChannel());
    }

    public MessageAction replyA(PresetBuilder preset, MessageChannel channel) {
        return reply.embedReply(preset.getEmbed(), channel);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String code) {
        return (T) parsedArgumentSet.getArgument(code);
    }

    public Map<String, ?> getArguments() {
        return parsedArgumentSet.getArguments();
    }

    public String getAliasUsed() {
        return aliasedUsed;
    }
}