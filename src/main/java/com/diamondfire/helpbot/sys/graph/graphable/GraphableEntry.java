package com.diamondfire.helpbot.sys.graph.graphable;

import java.util.Objects;

// Graphable Entries are displayed at bottom, and the amount of entries that are the same are calculated
// and put onto the app.graph.
public abstract class GraphableEntry<T> {

    private T entry;

    public GraphableEntry(T entry) {
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }

    @Override
    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphableEntry<?> that = (GraphableEntry<?>) o;
        return Objects.equals(entry, that.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry);
    }
}
