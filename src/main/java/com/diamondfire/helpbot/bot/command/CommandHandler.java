package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.executor.CommandExecutor;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.HashMap;

public class CommandHandler {

    private final HashMap<String, Command> CMDS = new HashMap<>();
    private final HashMap<String, Command> ALIASES = new HashMap<>();
    private final CommandExecutor executor = new CommandExecutor();

    public static Command getCommand(String name) {
        Command cmd = HelpBotInstance.getHandler().getCommands().get(name.toLowerCase());
        if (cmd == null) {
            cmd = HelpBotInstance.getHandler().getAliases().get(name.toLowerCase());
        }

        return cmd;
    }

    public void register(Command... commands) {
        for (Command command : commands) {
            this.CMDS.put(command.getName().toLowerCase(), command);
            for (String alias : command.getAliases()) {
                this.ALIASES.put(alias.toLowerCase(), command);
            }
        }

    }

    public void run(CommandEvent e) {
        executor.run(e);
    }

    public HashMap<String, Command> getCommands() {
        return CMDS;
    }

    public HashMap<String, Command> getAliases() {
        return ALIASES;
    }
}
