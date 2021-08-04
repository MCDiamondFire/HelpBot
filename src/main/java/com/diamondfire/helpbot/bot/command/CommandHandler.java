package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.command.disable.DisableCommandHandler;
import com.diamondfire.helpbot.bot.command.executor.CommandExecutor;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.events.CommandEvent;

import java.util.HashMap;

public class CommandHandler {
    
    private final HashMap<String, Command> CMDS = new HashMap<>();
    private final HashMap<String, Command> ALIASES = new HashMap<>();
    private final CommandExecutor COMMAND_EXECUTOR = new CommandExecutor();
    private final DisableCommandHandler DISABLED_COMMAND_HANDLER = new DisableCommandHandler();

    private static CommandHandler instance;
    
    private CommandHandler() {
        instance = this;
    }
    
    public void initialize() {
        DISABLED_COMMAND_HANDLER.initialize();
    }
    
    public static Command getCommand(String name) {
        Command cmd = CommandHandler.getInstance().getCommands().get(name.toLowerCase());
        if (cmd == null) {
            cmd = CommandHandler.getInstance().getAliases().get(name.toLowerCase());
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
    
    public void run(CommandEvent e, String[] args) {
        COMMAND_EXECUTOR.run(e, args);
    }
    
    public void run(CommandEvent e) {
        run(e, e.getRawArgs());
    }
    
    public HashMap<String, Command> getCommands() {
        return CMDS;
    }
    
    public HashMap<String, Command> getAliases() {
        return ALIASES;
    }
    
    public DisableCommandHandler getDisabledHandler() {
        return DISABLED_COMMAND_HANDLER;
    }
    
    public static CommandHandler getInstance() {
        return instance == null ? new CommandHandler() : instance;
    }
}