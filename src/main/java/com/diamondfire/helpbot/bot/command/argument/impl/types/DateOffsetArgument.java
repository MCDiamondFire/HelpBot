package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DateOffsetArgument extends AbstractSimpleValueArgument<Date> {

    @Override
    protected Date parse(@NotNull String argument) throws ArgumentException {
        Calendar calendar = Calendar.getInstance();
        int addedDays = 0;
        int offset = 0;

        for (int i = 0; i < argument.length(); i++) {
            char currentChar = argument.charAt(i);

            if (!Character.isDigit(currentChar)) {
                DateDuration dateDuration = DateDuration.fromChar(currentChar);
                if (dateDuration != null) {
                    addedDays += dateDuration.calculate(Integer.parseInt(argument.substring(offset, i)));
                } else {
                    throw new MalformedArgumentException("Unknown time unit: " + currentChar);
                }
                offset = i + 1;
            }
        }

        calendar.add(Calendar.DAY_OF_MONTH, addedDays);
        return calendar.getTime();
    }

    private enum DateDuration {
        DAY('d', 1),
        WEEK('w', 7),
        MONTH('m', 30);

        private final char id;
        private final int days;
        private static final Map<Character, DateDuration> charMap = new HashMap<>();

        static {
            for (DateDuration duration : values()) {
                charMap.put(duration.getId(), duration);
            }
        }

        DateDuration(char id, int days) {
            this.id = id;
            this.days = days;
        }

        public char getId() {
            return id;
        }

        public static DateDuration fromChar(char character) {
            return charMap.get(character);
        }

        public int calculate(int num) {
            return num * this.days;
        }
    }
}
