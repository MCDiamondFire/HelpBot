package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;
import java.util.concurrent.*;

public class CommandHandler {

    private final HashMap<String, Command> commands = new HashMap<>();
    private final HashMap<String, Command> aliases = new HashMap<>();
    private final ExecutorService POOL = Executors.newCachedThreadPool();

    public void register(Command... commands) {
        for (Command command : commands) {
            this.commands.put(command.getName().toLowerCase(), command);
            for (String alias : command.getAliases()) {
                this.aliases.put(alias.toLowerCase(), command);
            }
        }

    }

    public void run(CommandEvent e) {
        Command command = e.getCommand();

        if (command != null) {
            if (!command.getPermission().hasPermission(e.getMember())) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("No Permission!");
                builder.setDescription("Sorry, you do not have permission to use this command. Commands that you are able to use are listed in ?help.");
                builder.setFooter("Permission Required: " + command.getPermission().name());
                e.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            CompletableFuture.runAsync(() -> {
                        try {
                            command.run(e);
                        } catch (Exception error) {
                            Util.error(error, "Command error!");
                            error.printStackTrace();
                        }
                    }, POOL
            );
        }

    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public HashMap<String, Command> getAliases() {
        return aliases;
    }
}
