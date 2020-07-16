package com.diamondfire.helpbot.command.argument.impl.types;

import org.jetbrains.annotations.NotNull;

public abstract class Argument<T> {

    private T defaultValue;
    private boolean isOptional;

    public abstract T getValue(@NotNull String msg) throws IllegalArgumentException;

    public Argument<T> optional(T defaultValue) {
        this.defaultValue = defaultValue;
        this.isOptional = true;
        return this;
    }

    public boolean isOptional() {
        return isOptional;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue() {
        return (T) defaultValue;
    }
}
 