package com.diamondfire.helpbot.sys.graph.impl;


import com.diamondfire.helpbot.sys.graph.graphable.GraphableEntry;

import java.awt.*;
import java.util.ArrayList;

public abstract class Graph {

    private static final int WIDTH = 2000;
    private static final int HEIGHT = 1300;
    private static final int BORDER_WIDTH = 70;

    private ArrayList<GraphableEntry> entries = new ArrayList<>();

    public ArrayList<GraphableEntry> getEntries() {
        return entries;
    }

    public abstract void paintGraph(Graphics2D graphics);

}
