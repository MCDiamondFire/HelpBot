package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.disable.DisableCommandHandler;
import com.diamondfire.helpbot.bot.command.executor.CommandExecutor;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.command.slash.SlashCommands;
import com.diamondfire.helpbot.bot.events.commands.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

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
        Command cmd = CommandHandler.getInstance().getCommands().get(name.toLowerCase(Locale.ROOT));
        if (cmd == null) {
            cmd = CommandHandler.getInstance().getAliases().get(name.toLowerCase(Locale.ROOT));
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
    
    public void setupSlashCommands() {
        System.out.println("Initializing slash commands...");
        boolean slashCommands = HelpBotInstance.getConfig().useSlashCommands();
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
    
        if (slashCommands) {
            List<CommandData> commandDataList = CMDS.values().stream()
                    .map(command -> {
                        try {
                            return SlashCommands.createCommandData(command);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            System.out.println(String.format("Registering %s commands...", commandDataList.size()));
            
            guild.updateCommands()
                    .addCommands(commandDataList)
                    .queue(commands1 -> {
                        System.out.println("Commands registered. Setting up permissions...");
                        Map<String, Collection<? extends CommandPrivilege>> privilegeMap = new HashMap<>();
                    
                        for (net.dv8tion.jda.api.interactions.commands.Command command :
                                commands1) {
                            Command command1 = CMDS.get(command.getName());
                            List<CommandPrivilege> commandPrivileges = new ArrayList<>();
                            for (Permission perm :
                                    Permission.VALUES) {
                                if (command1.getPermission().getPermissionLevel() <= perm.getPermissionLevel())
                                    commandPrivileges.add(CommandPrivilege.enableRole(perm.getRole()));
                            }
                            privilegeMap.put(command.getId(), commandPrivileges);
                        }
                    
                        guild.updateCommandPrivileges(privilegeMap).queue(stringListMap -> {
                            System.out.println("Slash command permissions registered.");
                        }, Throwable::printStackTrace);
                    }, Throwable::printStackTrace);
        }
    }
    
    public void run(@NotNull SlashCommandEvent event) {
        COMMAND_EXECUTOR.run(event);
    }
    
    public void run(CommandEvent e, String[] args) {
        COMMAND_EXECUTOR.run(e, args);
    }
    
    public void run(MessageCommandEvent e) {
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