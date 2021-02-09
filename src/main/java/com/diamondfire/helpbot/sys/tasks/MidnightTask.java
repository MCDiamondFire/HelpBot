package com.diamondfire.helpbot.sys.tasks;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public interface MidnightTask extends LoopingTask {
    
    @Override
    default long getInitialStart() {
        LocalDateTime now = LocalDateTime.now(TimeZone.getTimeZone("EST").toZoneId());
        LocalDateTime nextRun = now.withHour(22).withMinute(0).withSecond(0);
        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }
        
        return Duration.between(now, nextRun).toMillis();
    }
    
    @Override
    default long getNextLoop() {
        return TimeUnit.DAYS.toMillis(1);
    }
    
}
