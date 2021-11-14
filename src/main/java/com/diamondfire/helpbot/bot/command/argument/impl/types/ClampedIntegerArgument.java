package com.diamondfire.helpbot.bot.command.argument.impl.types;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import com.diamondfire.helpbot.bot.events.commands.CommandEvent;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class ClampedIntegerArgument extends IntegerArgument {
    
    final int min;
    final int max;
    
    public ClampedIntegerArgument(int min) {
        this.min = min;
        this.max = Integer.MAX_VALUE;
    }
    
    public ClampedIntegerArgument(int min, int max) {
        this.min = min;
        this.max = max;
    }
    
    @Override
    public Integer parse(@NotNull String msg, CommandEvent event) throws ArgumentException {
        int num = super.parse(msg, event);
        
        if (num <= max && num >= min) {
            return num;
        } else {
            return Util.clamp(num, min, max);
        }
    }
    
    @Override
    public OptionData createOptionData(@NotNull String name, @NotNull String description, boolean isRequired) {
        return super.createOptionData(name, description, isRequired)
                .setRequiredRange(min, max);
    }
}
