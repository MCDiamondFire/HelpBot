package com.diamondfire.helpbot.command.arguments.value;

import com.diamondfire.helpbot.command.arguments.Argument;

public abstract class ValueArgument<T> extends Argument {

    T fallbackValue;

    public ValueArgument(String name, boolean isRequired) {
        super(name, isRequired);
        this.fallbackValue = null;
    }

    public ValueArgument(String name, T fallbackValue) {
        super(name, false);
        this.fallbackValue = fallbackValue;
    }

    public T getArg(String msg) {
        return msg.isEmpty() ? fallbackValue : getValue(msg);
    }

    public abstract T getValue(String msg);

    public boolean validate(String msg) {
        if (msg.isEmpty()) {
            return !isRequired();
        } else {
            if (validateValue(msg)) {
                return true;
            } else {
                return fallbackValue != null;
            }
        }

    }

    public T getFallbackValue() {
        return fallbackValue;
    }

    protected abstract boolean validateValue(String msg);



}
