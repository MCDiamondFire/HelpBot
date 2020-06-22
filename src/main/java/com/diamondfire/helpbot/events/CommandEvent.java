package com.diamondfire.helpbot.events;

import com.diamondfire.helpbot.instance.BotInstance;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

public class CommandEvent extends GuildMessageReceivedEvent {

    private final String[] arguments;
    private final String command;

    public CommandEvent(JDA api, long responseNumber, Message message) {
        super(api, responseNumber, message);
        String prefix = BotInstance.getConfig().getPrefix();
        String[] rawArgs = getMessage().getContentDisplay().split(" ");
        String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);

        this.command = rawArgs[0].substring(prefix.length());
        this.arguments = args;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String getParsedArgs() {
        return String.join(" ", getArguments());
    }

    public String getCommand() {
        return command;
    }
}
