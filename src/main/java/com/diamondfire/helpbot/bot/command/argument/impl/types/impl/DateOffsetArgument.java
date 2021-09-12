package com.diamondfire.helpbot.bot.command.argument.impl.types.impl;

import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractOffsetArgument;

import java.util.Calendar;

public class DateOffsetArgument extends AbstractOffsetArgument {
    
    public DateOffsetArgument() {
        super();
    }
    
    @Override
    protected Duration[] getDurations() {
        return new Duration[]{
                new Duration(Calendar.DAY_OF_MONTH, 'd'),
                new Duration(Calendar.DAY_OF_WEEK, 'w'),
                new Duration(Calendar.MONTH, 'm'),
                new Duration(Calendar.YEAR, 'y'),
        };
    }
    
}
