package com.diamondfire.helpbot.bot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

import java.text.*;
import java.util.Date;

public class DateArgument extends Argument<Date> {
    public SimpleDateFormat format;

    public DateArgument() {
        this.format = new SimpleDateFormat("MM/dd/yy");
    }

    public DateArgument(String format) {
        this.format = new SimpleDateFormat(format);
    }

    @Override
    public Date getValue(@NotNull String msg) throws IllegalArgumentException {
        try {
            return format.parse(msg);
        } catch (ParseException parseException) {
            throw new IllegalArgumentException("Invalid date provided! Format is " + format.toPattern());
        }
    }
}
