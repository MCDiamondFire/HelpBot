package com.diamondfire.helpbot.sys.graph.generators.context;

import com.diamondfire.helpbot.sys.graph.generators.TimeMode;

public class SupportGraphContext extends TimeGraphContext {
    
    private final String support;
    
    public SupportGraphContext(TimeMode mode, int num, String support) {
        super(mode, num);
        this.support = support;
    }
    
    public String getSupport() {
        return support;
    }
}
