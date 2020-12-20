package com.diamondfire.helpbot.sys.tasks.impl;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.LoopingTask;
import com.diamondfire.helpbot.util.Util;

import java.util.concurrent.TimeUnit;

public class NameUpdateTask implements LoopingTask {
    
    @Override
    public long getInitialStart() {
        return 0;
    }
    
    @Override
    public long getNextLoop() {
        return TimeUnit.MINUTES.toMillis(10);
    }
    
    @Override
    public void run() {
        Util.updateGuild(HelpBotInstance.getJda().getGuildById(HelpBotInstance.DF_GUILD));
    }
}
