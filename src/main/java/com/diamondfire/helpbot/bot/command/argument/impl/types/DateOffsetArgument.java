package com.diamondfire.helpbot.bot.command.argument.impl.types;

import java.util.Calendar;

public class DateOffsetArgument extends AbstractOffsetArgument {
    
    @Override
    protected Duration[] getDurations() {
        return new Duration[]{
                new Duration(Calendar.DAY_OF_MONTH, 'd'),
                new Duration(Calendar.DAY_OF_WEEK, 'w'),
                new Duration(Calendar.MONTH, 'm'),
        };
    }
    
}
