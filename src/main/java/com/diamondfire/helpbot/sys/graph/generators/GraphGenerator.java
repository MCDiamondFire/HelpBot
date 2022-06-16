package com.diamondfire.helpbot.sys.graph.generators;

public interface GraphGenerator<T> {
    
    byte[] createGraph(T context);
    
}
