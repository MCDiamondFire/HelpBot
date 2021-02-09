package com.diamondfire.helpbot.sys.graph.generators;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public enum TimeMode {
    
    HOURLY("Hourly", "%d-%Hh", (num) -> num),
    DAILY("Daily", "%y-%m-%d", (num) -> (int) TimeUnit.DAYS.toHours(num)),
    WEEKLY("Weekly", "%y-%m-%v", (num) -> (int) TimeUnit.DAYS.toHours(num * 7L)),
    MONTHLY("Monthly", "%y-%m", (num) -> (int) TimeUnit.DAYS.toHours(num * 30L)),
    YEARLY("Yearly", "%y", (num) -> (int) TimeUnit.DAYS.toHours(num * 365L)),
    ;
    
    private final String mode;
    private final String dateFormat;
    
    private final Function<Integer, Integer> unitConversion;
    
    TimeMode(String mode, String dateFormat, Function<Integer, Integer> unitConversion) {
        this.mode = mode;
        this.dateFormat = dateFormat;
        this.unitConversion = unitConversion;
    }
    
    public String getMode() {
        return mode;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public Function<Integer, Integer> getUnitConversion() {
        return unitConversion;
    }
    
    @Override
    public String toString() {
        return mode;
    }
}
