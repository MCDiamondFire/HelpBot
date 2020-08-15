package com.diamondfire.helpbot.bot.command.argument.impl.types;


import com.diamondfire.helpbot.bot.command.argument.impl.parsing.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

public abstract class Argument<T> {

    private T defaultValue;
    private boolean isOptional;

    public abstract T parseValue(@NotNull String msg) throws ArgumentException;

    public Argument<T> optional(T defaultValue) {
        this.defaultValue = defaultValue;
        this.isOptional = true;
        return this;
    }

    public boolean isOptional() {
        return isOptional;
    }

    @SuppressWarnings("unchecked")
    public <D> D getDefaultValue() {
        return (D) defaultValue;
    }

}
 