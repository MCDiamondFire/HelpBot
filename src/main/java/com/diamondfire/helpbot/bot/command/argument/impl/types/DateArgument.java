package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.text.*;
import java.util.Date;

public class DateArgument extends AbstractSimpleValueArgument<Date> {
    
    public final SimpleDateFormat format;
    
    public DateArgument() {
        this.format = new SimpleDateFormat("MM/dd/yy");
    }
    
    public DateArgument(String format) {
        this.format = new SimpleDateFormat(format);
    }
    
    @Override
    public Date parse(@NotNull String msg) throws ArgumentException {
        try {
            return format.parse(msg);
        } catch (ParseException parseException) {
            throw new MalformedArgumentException("Invalid date provided, this date argument's format is " + format.toPattern());
        }
    }
}
