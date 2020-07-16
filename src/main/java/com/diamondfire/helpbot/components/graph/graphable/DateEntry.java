package com.diamondfire.helpbot.components.graph.graphable;

import java.util.Date;

public class DateEntry extends GraphableEntry<Date> {

    public DateEntry(Date entry) {
        super(entry);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String toString() {
        Date date = getEntry();
        return (date.getMonth() + 1) + "/" + (date.getDate()) + "/" + (date.getYear() + 1900);
    }

}
