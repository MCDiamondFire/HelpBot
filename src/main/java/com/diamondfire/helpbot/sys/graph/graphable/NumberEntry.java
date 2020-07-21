package com.diamondfire.helpbot.sys.graph.graphable;

public class NumberEntry extends GraphableEntry<Integer> {

    public NumberEntry(Integer entry) {
        super(entry);
    }

    @Override
    public String toString() {
        return getEntry().toString();
    }
}
