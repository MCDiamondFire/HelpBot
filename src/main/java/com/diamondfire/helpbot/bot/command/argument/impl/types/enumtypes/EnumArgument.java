package com.diamondfire.helpbot.bot.command.argument.impl.types.enumtypes;

import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.*;
import com.diamondfire.helpbot.bot.command.argument.impl.types.AbstractSimpleValueArgument;
import com.diamondfire.helpbot.bot.events.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class EnumArgument<E extends Enum<E> & EnumArgument.InputEnum> extends AbstractSimpleValueArgument<E> {
    
    private EnumSet<E> associatedEnum;
    
    public EnumArgument<E> setEnum(Class<E> anEnum) {
        associatedEnum = EnumSet.allOf(anEnum);
        return this;
    }
    
    @Override
    protected E parse(@NotNull String argument, CommandEvent event) throws ArgumentException {
        for (E enumValue : associatedEnum) {
            if (argument.equals(enumValue.getName())) {
                return enumValue;
            }
        }
        
        throw new MalformedArgumentException("This is not a valid option! Choose from: " + getFormattedOptions());
    }
    
    private String getFormattedOptions() {
        return getFormattedOptions(associatedEnum);
    }
    
    public static <T extends Enum<T> & InputEnum> String getFormattedOptions(@NotNull Class<T> anEnum) {
        return getFormattedOptions(EnumSet.allOf(anEnum));
    }
    
    public static String getFormattedOptions(@NotNull EnumSet<? extends InputEnum> anEnum) {
        return "`" +
                anEnum.stream()
                        .filter(e -> !e.isHidden())
                        .map(InputEnum::getName)
                        .collect(Collectors.joining("` `"))
                + "`";
    }
    
    protected interface InputEnum {
        
        String getName();
        
        boolean isHidden();
    }
}
