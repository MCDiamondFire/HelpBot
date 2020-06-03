package com.diamondfire.helpbot.command.arguments.value;

import com.diamondfire.helpbot.util.Util;

public class LimitedIntegerArg extends IntegerArg {

    final int min;
    final int max;

    public LimitedIntegerArg(String name, int min, int max) {
        super(name, false);

        this.min = min;
        this.max = max;
    }

    public LimitedIntegerArg(String name, int min, int max, int fallbackValue) {
        super(name, fallbackValue);

        this.min = min;
        this.max = max;
    }

    @Override
    public Integer getValue(String msg) {
        int num = Integer.parseInt(msg);

        if (num <= max && num >= min) {
            return num;
        } else {
            return Util.clamp(num, min, max);
        }
    }

    @Override
    public String failMessage() {
        return "Text must contain a valid integer!";
    }

    @Override
    public String toString() {
        return "Limited Number";
    }
}
