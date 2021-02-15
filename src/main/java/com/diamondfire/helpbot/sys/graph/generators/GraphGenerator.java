package com.diamondfire.helpbot.sys.graph.generators;

import java.io.File;

public interface GraphGenerator<T> {
    
    File createGraph(T context);
    
}
