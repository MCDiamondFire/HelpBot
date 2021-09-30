package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.restart.RestartHandler;
import com.diamondfire.helpbot.sys.rolereact.RoleReactListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReadyEvent extends ListenerAdapter {
    
    @Override
    public void onReady(@Nonnull net.dv8tion.jda.api.events.ReadyEvent event) {
        super.onReady(event);
        
        RestartHandler.recover(event.getJDA());
        HelpBotInstance.getScheduler().initialize();
        
        event.getJDA().addEventListener(new RoleReactListener());
        
        CommandHandler.getInstance().setupSlashCommands();
    }
}
