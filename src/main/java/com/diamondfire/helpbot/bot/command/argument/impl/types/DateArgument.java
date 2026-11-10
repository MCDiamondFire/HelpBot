package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.text.*;
import java.util.*;

public class DateArgument extends AbstractSimpleValueArgument<Date> {
    
    public final SimpleDateFormat format;
    
    public DateArgument() {
        this.format = new SimpleDateFormat("MM/dd/yy");
    }
    
    public DateArgument(String format) {
        this.format = new SimpleDateFormat(format);
    }
    
    @Override
    public List<String> getCompletions(@NotNull String input) {
        SimpleDateFormat completionFormat = new SimpleDateFormat(format.toPattern());
        List<String> dates = new ArrayList<>();
        for (int daysAgo : new int[]{0, 1, 7, 30, 90, 365}) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -daysAgo);
            dates.add(completionFormat.format(calendar.getTime()));
        }
        
        return dates;
    }
    
    @Override
    public boolean hasCompletions() {
        return true;
    }
    
    @Override
    public Date parse(@NotNull String msg, CommandEvent event) throws ArgumentException {
        try {
            return format.parse(msg);
        } catch (ParseException parseException) {
            throw new MalformedArgumentException("Invalid date provided, this date argument's format is " + format.toPattern());
        }
    }
}
