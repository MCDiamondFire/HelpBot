package com.diamondfire.helpbot.sys.graph.graphable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateEntry extends GraphableEntry<Date> {
    
    public DateEntry(Date entry) {
        super(entry);
    }
    
    @Override
    public String toString() {
        Date date = getEntry();
        return new SimpleDateFormat("dd/MM/yy").format(date);
    }
    
}
