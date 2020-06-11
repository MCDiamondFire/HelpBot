package com.diamondfire.helpbot.components.codedatabase;

import com.diamondfire.helpbot.command.impl.other.FetchDataCommand;
import com.diamondfire.helpbot.instance.BotInstance;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoRefreshDBTask implements Runnable {

    public static void initialize() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new AutoRefreshDBTask(),
                1,
                1,
                TimeUnit.DAYS);
    }

    @Override
    public void run() {
        new FetchDataCommand().setup(BotInstance.getJda().getTextChannelById(705205549498892299L));
    }
}
