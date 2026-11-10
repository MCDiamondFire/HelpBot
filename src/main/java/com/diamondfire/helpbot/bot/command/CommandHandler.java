package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.command.disable.DisableCommandHandler;
import com.diamondfire.helpbot.bot.command.executor.CommandExecutor;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.slash.SlashCommands;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.*;

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
    
    public List<SlashCommandData> getSlashCommands() {
        List<SlashCommandData> commands = new ArrayList<>();
        for (Command command : CMDS.values()) {
            addSlashCommand(commands, command, command.getName());
        }
        
        List<Command> sorted = CMDS.values().stream().sorted(Comparator.comparing(Command::getName)).toList();
        int maxAliases = sorted.stream().mapToInt(command -> command.getAliases().length).max().orElse(0);
        for (int i = 0; i < maxAliases; i++) {
            for (Command command : sorted) {
                if (i >= command.getAliases().length || CMDS.containsKey(command.getAliases()[i].toLowerCase())) {
                    continue;
                }
                
                if (commands.size() >= Commands.MAX_SLASH_COMMANDS) {
                    System.out.println("Slash command limit reached, skipping alias " + command.getAliases()[i]);
                    continue;
                }
                
                addSlashCommand(commands, command, command.getAliases()[i].toLowerCase());
            }
        }
        
        return commands;
    }
    
    private void addSlashCommand(List<SlashCommandData> commands, Command command, String name) {
        try {
            commands.add(SlashCommands.create(command, name));
        } catch (Exception exception) {
            System.err.println("Failed to create slash command " + name);
            exception.printStackTrace();
        }
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