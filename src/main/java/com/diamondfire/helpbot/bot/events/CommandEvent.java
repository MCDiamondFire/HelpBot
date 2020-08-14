package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.ParsedArgumentSet;
import com.diamondfire.helpbot.bot.command.argument.impl.parsing.parser.ArgumentParser;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.*;
import com.diamondfire.helpbot.bot.HelpBotInstance;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.*;

public class CommandEvent extends GuildMessageReceivedEvent {

    private ParsedArgumentSet parsedArgumentSet = null;
    private Command command = null;
    private final ReplyHandler reply = new ReplyHandler();

    public CommandEvent(JDA api, long responseNumber, Message message) throws IllegalArgumentException {
        super(api, responseNumber, message);
        String[] rawArgs = getMessage().getContentDisplay().split(" ");
        String commandPrefix = rawArgs[0].substring(HelpBotInstance.getConfig().getPrefix().length()).toLowerCase();
        Command command = CommandHandler.getCommand(commandPrefix);

        if (command == null) return;

        this.command = command;

        this.parsedArgumentSet = ArgumentParser.parseArgs(command.getArguments(), Arrays.copyOfRange(rawArgs, 1, rawArgs.length));
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
}
