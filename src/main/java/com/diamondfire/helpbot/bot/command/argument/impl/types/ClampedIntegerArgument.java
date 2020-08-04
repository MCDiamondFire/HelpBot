package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.util.Util;
import org.jetbrains.annotations.NotNull;

public class ClampedIntegerArgument extends IntegerArgument {

    final int min;
    final int max;

    public ClampedIntegerArgument(int min) {
        super();

        this.min = min;
        this.max = Integer.MAX_VALUE;
    }

    public ClampedIntegerArgument(int min, int max) {
        super();

        this.min = min;
        this.max = max;
    }

    @Override
    public Integer getValue(@NotNull String msg) throws IllegalArgumentException {
        int num = super.getValue(msg);

        if (num <= max && num >= min) {
            return num;
        } else {
            return Util.clamp(num, min, max);
        }
    }
}
