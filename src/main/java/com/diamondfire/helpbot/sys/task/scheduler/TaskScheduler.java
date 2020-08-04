package com.diamondfire.helpbot.sys.task.scheduler;


import java.util.concurrent.*;

public class TaskScheduler {

    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);


    public static void scheduleTask(Runnable runnable, long milli) {
        service.schedule(runnable,milli,TimeUnit.MILLISECONDS);
    }

    public static void scheduleFixedTask(Runnable runnable, long initialDelay, long repeatDelay) {
        service.scheduleAtFixedRate(runnable,initialDelay,repeatDelay, TimeUnit.MILLISECONDS);
    }
}
