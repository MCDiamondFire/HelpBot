package com.diamondfire.helpbot.components.graph.graphable;

import java.sql.Timestamp;

public class TimeStampEntry extends GraphableEntry<Timestamp> {

    public TimeStampEntry(Timestamp entry) {
        super(entry);
    }

    @Override
    public String toString() {
        Timestamp timestamp = getEntry();
        return timestamp.toString();
    }

}
