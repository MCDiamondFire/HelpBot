package com.diamondfire.helpbot.sys.graph.impl;


import com.diamondfire.helpbot.sys.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.sys.graph.graphable.GraphableEntry;
import org.jetbrains.annotations.Contract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class ChartGraphBuilder {

    private String graphName = "";

    @Contract("_, -> this")
    public ChartGraphBuilder setGraphName(String graphName) {
        if (graphName == null) throw new IllegalArgumentException("Graph name cannot be null");
        this.graphName = graphName;
        return this;
    }

    public File createGraphFromCollection(Collection<? extends GraphableEntry<?>> entryList) {
        Map<GraphableEntry<?>, Integer> entries = new LinkedHashMap<>();
        for (GraphableEntry<?> entry : entryList) {
            entries.computeIfPresent(entry, (key, value) -> value + 1);
            entries.putIfAbsent(entry, 1);
        }
        return createGraph(entries);
    }


    public File createGraph(Map<GraphableEntry<?>, Integer> entries) {
        BoxGraph graph = new ChartGraph(graphName);
        File graphFile;
        try {
            graphFile = ExternalFileUtil.generateFile("graph.png");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        BufferedImage image = new BufferedImage(2250, 1420, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.setFont(graphics.getFont().deriveFont(25F));
        graphics.setColor(new Color(53, 54, 59));
        graphics.fillRect(0, 0, 2250, 1420);
        graph.getEntries().putAll(entries);
        //graphics.drawRect(BORDER_WIDTH, BORDER_WIDTH, WIDTH, HEIGHT);
        graph.paintGraph(graphics);
        //Generate values and their numbers

        try {
            ImageIO.write(image, "png", graphFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graphFile;
    }
}
