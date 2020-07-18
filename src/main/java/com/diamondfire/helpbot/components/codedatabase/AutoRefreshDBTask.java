package com.diamondfire.helpbot.components.codedatabase;

import com.diamondfire.helpbot.command.impl.other.FetchDataCommand;
import com.diamondfire.helpbot.instance.BotInstance;

import java.time.*;
import java.util.concurrent.*;

public class AutoRefreshDBTask implements Runnable {

    public static void initialize() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);

        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        long timeTillMidnight = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new AutoRefreshDBTask(),
                timeTillMidnight,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        new FetchDataCommand().setup(BotInstance.getJda().getTextChannelById(705205549498892299L));
    }
}
