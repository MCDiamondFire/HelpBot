package com.diamondfire.helpbot.sys.graph.generators.context;

import com.diamondfire.helpbot.sys.graph.generators.TimeMode;

public class TimeGraphContext {
    
    private final TimeMode mode;
    private final int num;
    
    public TimeGraphContext(TimeMode mode, int num) {
        this.mode = mode;
        this.num = num;
    }
    
    public TimeMode getMode() {
        return mode;
    }
    
    public int getNum() {
        return num;
    }
}
