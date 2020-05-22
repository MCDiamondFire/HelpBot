package com.owen1212055.helpbot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class CommandEvent extends GuildMessageReceivedEvent {
    private String[] arguments;
    private String command;

    public CommandEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message) {
        super(api, responseNumber, message);
        String[] rawArgs = getMessage().getContentDisplay().split(" ");
        String[] args = Arrays.copyOfRange(getMessage().getContentDisplay().split(" "), 1, rawArgs.length);

        this.command = rawArgs[0].substring(1);
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
