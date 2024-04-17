package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.restart.RestartHandler;
import com.diamondfire.helpbot.sys.rolereact.RoleReactListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyEvent extends ListenerAdapter {
    
    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        RestartHandler.recover(event.getJDA());
        HelpBotInstance.getScheduler().initialize();
        
        event.getJDA().addEventListener(new RoleReactListener());
    }
}
