package com.diamondfire.helpbot.command.arguments;

public abstract class Argument {
    private final String name;
    private final boolean isRequired;

    public Argument(String name, boolean isRequired) {
        this.name = name;
        this.isRequired = isRequired;
    }


    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public abstract boolean validate(String msg);

    public String failMessage() {
        return "The argument you provided was invalid.";
    }

    @Override
    public String toString() {
        return getName();
    }
}
