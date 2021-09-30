package com.diamondfire.helpbot.bot.command;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.disable.DisableCommandHandler;
import com.diamondfire.helpbot.bot.command.executor.CommandExecutor;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.command.permissions.Permission;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandHandler extends ListenerAdapter {
    
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
    
    public void setupSlashCommands() {
        boolean slashCommands = HelpBotInstance.getConfig().useSlashCommands();
        Guild guild = HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD);
    
        if (slashCommands) {
            guild.updateCommands()
                    .addCommands(CMDS.values().stream()
                            .map(SlashCommands::createCommandData)
                            .toList())
                    .queue(commands1 -> {
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
                    
                        guild.updateCommandPrivileges(privilegeMap).queue();
                    });
        }
    }
    
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        COMMAND_EXECUTOR.run(event)
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