package com.diamondfire.helpbot.components.graph.impl;


import com.diamondfire.helpbot.components.externalfile.ExternalFileUtil;
import com.diamondfire.helpbot.components.graph.graphable.GraphableEntry;
import org.jetbrains.annotations.Contract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;

public class ChartGraphBuilder {

    private String graphName = "";

    @Contract("_, -> this")
    public ChartGraphBuilder setGraphName(String graphName) {
        if (graphName == null) throw new IllegalArgumentException("Graph name cannot be null");
        this.graphName = graphName;
        return this;
    }

    public File createGraph(Collection<? extends GraphableEntry<?>> entryList) {
        Graph graph = new ChartGraph(graphName);
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
        graph.getEntries().addAll(entryList);
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
