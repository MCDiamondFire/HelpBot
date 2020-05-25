package com.diamondfire.helpbot.command;

import com.diamondfire.helpbot.command.impl.Command;
import com.diamondfire.helpbot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;

public class CommandHandler {

    private HashMap<String, Command> commands = new HashMap<>();

    public void register(Command... commands) {
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }

    }

    public void run(CommandEvent e) {
        Command commandToRun = commands.get(e.getCommand());
        if (commandToRun != null) {
            if (commandToRun.getPermission().hasPermission(e.getMember())) {
                if (commandToRun.getArgument().validate(e.getParsedArgs())) {

                    try {
                        commandToRun.run(e);
                    } catch (Exception error) {
                        Util.error(error, "Command error!");
                        error.printStackTrace();
                    }
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Invalid Arguments!");
                    builder.setDescription(commandToRun.getArgument().failMessage());
                    e.getChannel().sendMessage(builder.build()).queue();
                }

            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("No Permission!");
                builder.setDescription("Sorry, you do not have permission to use this command. Commands that you are able to use are listed in ?help.");
                e.getChannel().sendMessage(builder.build()).queue();
            }
        }


    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
