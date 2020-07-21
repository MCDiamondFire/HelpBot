package com.diamondfire.helpbot.sys.graph.graphable;

public class StringEntry extends GraphableEntry<String> {

    public StringEntry(String entry) {
        super(entry);
    }

    @Override
    public String toString() {
        return getEntry();
    }
}
