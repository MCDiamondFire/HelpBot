package com.diamondfire.helpbot.events;

import com.diamondfire.helpbot.command.argument.ParsedArgumentSet;
import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.command.reply.*;
import com.diamondfire.helpbot.instance.BotInstance;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.*;

public class CommandEvent extends GuildMessageReceivedEvent {

    private ParsedArgumentSet parsedArgumentSet = null;
    private Command command = null;
    private ReplyHandler reply = new ReplyHandler();

    public CommandEvent(JDA api, long responseNumber, Message message) {
        super(api, responseNumber, message);
        String prefix = BotInstance.getConfig().getPrefix();
        String[] rawArgs = getMessage().getContentDisplay().split(" ");
        String commandPrefix = rawArgs[0].substring(prefix.length()).toLowerCase();
        Command command = BotInstance.getHandler().getCommands().get(commandPrefix);
        if (command == null) {
            command = BotInstance.getHandler().getAliases().get(commandPrefix);
        }

        if (command == null) {
            return;
        }

        // This will need to be refactored soon. I really didn't want it to turn out like this.. :(
        // I really do not know what I am doing.
        // TODO Isolate the values from the arguments themselves.
        this.command = command;
        try {
            this.parsedArgumentSet = new ParsedArgumentSet(command.getArguments(), Arrays.copyOfRange(rawArgs, 1, rawArgs.length));
        } catch (IllegalArgumentException e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Invalid Argument!");
            builder.setColor(Color.RED);
            builder.setDescription(e.getMessage());

            message.getChannel().sendMessage(builder.build()).queue();
            throw new IllegalArgumentException();
        }


    }

    public Command getCommand() {
        return command;
    }

    public void reply(PresetBuilder preset) {
        reply.reply(preset.getEmbed(), getChannel()).queue();
    }

    public void reply(PresetBuilder preset, TextChannel channel) {
        reply.reply(preset.getEmbed(), channel).queue();
    }

    public MessageAction replyA(PresetBuilder preset) {
        return reply.reply(preset.getEmbed(), getChannel());
    }

    public MessageAction replyA(PresetBuilder preset, TextChannel channel) {
        return reply.reply(preset.getEmbed(), channel);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String code) {
        return (T) parsedArgumentSet.getArgument(code);
    }

    public Map<String, ?> getArguments() {
        return parsedArgumentSet.getArguments();
    }
}
