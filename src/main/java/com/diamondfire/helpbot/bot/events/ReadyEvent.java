package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.bot.command.CommandHandler;
import com.diamondfire.helpbot.bot.restart.RestartHandler;
import com.diamondfire.helpbot.sys.report.ReportListener;
import com.diamondfire.helpbot.sys.rolereact.RoleReactListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyEvent extends ListenerAdapter {
    
    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        RestartHandler.recover(event.getJDA());
        HelpBotInstance.getScheduler().initialize();
        
        Guild guild = event.getJDA().getGuildById(HelpBotInstance.DF_GUILD);
        if (guild != null) {
            guild.updateCommands().addCommands(CommandHandler.getInstance().getSlashCommands()).queue();
        }
        
        event.getJDA().addEventListener(new RoleReactListener(), new ReportListener());
    }
}
