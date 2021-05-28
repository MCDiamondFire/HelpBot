package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.impl.Command;
import com.diamondfire.helpbot.bot.restart.RestartHandler;
import com.diamondfire.helpbot.sys.rolereact.RoleReactListener;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;

import javax.annotation.Nonnull;
import java.util.*;

public class ReadyEvent extends ListenerAdapter {
    
    @Override
    public void onReady(@Nonnull net.dv8tion.jda.api.events.ReadyEvent event) {
        super.onReady(event);
        
        RestartHandler.recover(event.getJDA());
        HelpBotInstance.getScheduler().initialize();
        
        event.getJDA().addEventListener(new RoleReactListener());
    
    
        Guild guild = event.getJDA().getGuildById(HelpBotInstance.DF_GUILD);
        guild.retrieveCommands().queue((commands) -> {
            List<Command> helpbotCommands = new ArrayList<>(HelpBotInstance.getHandler().getCommands().values());
        
            Set<String> commandNames = new HashSet<>();
            for (Command command : helpbotCommands) {
                commandNames.add(command.getName());
            }
        
            // Delete invalid commands
            for (net.dv8tion.jda.api.interactions.commands.Command cmd : commands) {
                if (!commandNames.contains(cmd.getName())) {
                    //cmd.delete().queue();
                } else {
                    helpbotCommands.removeIf((command) -> cmd.getName().equals(command.getName()));
                }
            }
        
            CommandUpdateAction action = guild.updateCommands();
            for (Command command : helpbotCommands) {
                action.addCommands(new CommandData(command.getName(), StringUtil.trim(command.getHelpContext().getDescription(), 100)));
            }
            action.queue();
        });
    }
}
