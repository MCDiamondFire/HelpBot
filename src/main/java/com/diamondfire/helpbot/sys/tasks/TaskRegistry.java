package com.diamondfire.helpbot.sys.tasks;

import com.diamondfire.helpbot.bot.HelpBotInstance;
import com.diamondfire.helpbot.sys.tasks.impl.*;

import java.util.concurrent.*;

public class TaskRegistry {
    
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    
    public void initialize() {
        if (HelpBotInstance.getConfig().isDevBot()) return;
        
        register(
                //new CodeDatabaseTask(),
                new GraphChannelTask(),
                //new RefreshCreditsTask(),
                new SupporterClassTask(),
                new NameUpdateTask()
        );
        
        SupportUnexcuseTask.prepare();
        MuteExpireTask.prepare();
    }
    
    private void register(LoopingTask... tasks) {
        for (LoopingTask task : tasks) {
            scheduledExecutorService.scheduleAtFixedRate(task,
                    task.getInitialStart(),
                    task.getNextLoop(),
                    TimeUnit.MILLISECONDS);
        }
    }
    
    public void schedule(Runnable runnable, long timeMs) {
        scheduledExecutorService.schedule(runnable, timeMs, TimeUnit.MILLISECONDS);
    }
    
    public void schedule(OneTimeTask task) {
        schedule(task, task.getExecution());
    }
    
}
