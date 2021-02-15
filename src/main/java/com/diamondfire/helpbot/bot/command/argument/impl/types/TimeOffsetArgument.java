package com.diamondfire.helpbot.bot.command.argument.impl.types;

import java.util.Calendar;

public class TimeOffsetArgument extends AbstractOffsetArgument {
    
    public TimeOffsetArgument() {
        super();
    }
    
    public TimeOffsetArgument(boolean reverse) {
        super(reverse);
    }
    
    @Override
    protected Duration[] getDurations() {
        return new Duration[]{
                new Duration(Calendar.SECOND, 's'),
                new Duration(Calendar.MINUTE, 'm'),
                new Duration(Calendar.HOUR, 'h'),
                new Duration(Calendar.DATE, 'd'),
        };
    }
    
}
