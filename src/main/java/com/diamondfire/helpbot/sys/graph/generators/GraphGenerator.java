package com.diamondfire.helpbot.sys.graph.generators;

import java.io.File;

public interface GraphGenerator {
    
    File createGraph(TimeMode timeMode, int duration);
    
}
