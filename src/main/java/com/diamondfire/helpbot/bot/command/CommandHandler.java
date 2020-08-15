package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.executor.CommandExecutor;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.reply.PresetBuilder;
import com.diamondfire.helpbot.bot.command.reply.feature.informative.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import com.diamondfire.helpbot.sys.disablecmds.DisableCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;
import java.util.concurrent.*;

public class CommandHandler {

    private final HashMap<String, Command> CMDS = new HashMap<>();
    private final HashMap<String, Command> ALIASES = new HashMap<>();
    private final CommandExecutor executor = new CommandExecutor();

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

    public static Command getCommand(String name) {
        Command cmd = HelpBotInstance.getHandler().getCommands().get(name.toLowerCase());
        if (cmd == null) {
            cmd = HelpBotInstance.getHandler().getAliases().get(name.toLowerCase());
        }

        return cmd;
    }

    public HashMap<String, Command> getCommands() {
        return CMDS;
    }

    public HashMap<String, Command> getAliases() {
        return ALIASES;
    }
}
