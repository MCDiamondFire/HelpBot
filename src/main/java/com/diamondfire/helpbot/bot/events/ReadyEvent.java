package com.diamondfire.helpbot.bot.events;

import com.diamondfire.helpbot.bot.restart.RestartHandler;
import com.diamondfire.helpbot.df.codeinfo.codedatabase.AutoRefreshDBTask;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ReadyEvent extends ListenerAdapter {

    @Override
    public void onReady(@Nonnull net.dv8tion.jda.api.events.ReadyEvent event) {
        super.onReady(event);

        AutoRefreshDBTask.initialize();
        RestartHandler.recover(event.getJDA());
    }
}
