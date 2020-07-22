package com.diamondfire.helpbot.sys.graph.impl;


import com.diamondfire.helpbot.sys.graph.graphable.GraphableEntry;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.*;

public class ChartGraph extends BoxGraph {

    public ChartGraph(String graphName) {
        super(graphName);
    }

    @Override
    void paintInner(Graphics2D graphics) {
        Map<GraphableEntry<?>, Integer> tickRegistry = new HashMap<>();
        Map<Integer, Integer> sideTickRegistry = new HashMap<>();

        // First we need to get all the point and count the number of occurrences each one has.
        LinkedHashMap<GraphableEntry<?>, Integer> points = new LinkedHashMap<>();
        for (GraphableEntry<?> entry : getEntries()) {
            points.computeIfPresent(entry, (key, value) -> value + 1);
            points.putIfAbsent(entry, 1);
        }

        int maxPoints = points.size() - 1;
        int valueIndex = 0;
        int currentTickBound = 0;

        for (Map.Entry<GraphableEntry<?>, Integer> entry : points.entrySet()) {
            int x = (valueIndex * (GRAPH_WIDTH) / maxPoints) + BORDER_SIZE + X_OFFSET;
            tickRegistry.put(entry.getKey(), x);

            // Here we get the font metrics and current X to see if it's okay to put a tick there.
            if (currentTickBound <= x + graphics.getFontMetrics().stringWidth(entry.getKey().toString()) / 2) {
                currentTickBound = x + 220;
                renderTick(graphics, x, HEIGHT, entry.getKey().toString() + "");
            }
            valueIndex++;
        }

        // Get the largest amount of values.
        int largestNumber = points.values().stream()
                .max(Comparator.comparingInt(Integer::intValue))
                .orElse(0);

        // Get the total height of the graph.
        int totalHeight = HEIGHT - BORDER_SIZE;
        currentTickBound = HEIGHT;
        for (int i = 0; i <= largestNumber; i++) {
            int y = (int) (HEIGHT - (i * (Math.ceil(totalHeight) / largestNumber)));
            sideTickRegistry.putIfAbsent(i, y);

            if (currentTickBound >= y) {
                currentTickBound = y - graphics.getFontMetrics().getHeight();
                renderSideTick(graphics, X_OFFSET + (BORDER_SIZE / 2), y, i + "");
            }

        }

        // Here we iterate through
        Point lastPoint = null;
        GeneralPath boldPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        GeneralPath translucentShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        translucentShape.moveTo(BORDER_SIZE + X_OFFSET, HEIGHT);
        for (Map.Entry<GraphableEntry<?>, Integer> entry : points.entrySet()) {
            int y = sideTickRegistry.get(entry.getValue());
            int x = tickRegistry.get(entry.getKey());

            if (lastPoint != null) {
                boldPath.lineTo(lastPoint.x, lastPoint.y);
                translucentShape.lineTo(lastPoint.x, lastPoint.y);
            } else {
                boldPath.moveTo(x, y);
            }
            lastPoint = new Point(x, y);
            valueIndex++;

        }
        boldPath.lineTo(lastPoint.x, lastPoint.y);
        translucentShape.lineTo(lastPoint.x, lastPoint.y);
        translucentShape.lineTo(lastPoint.x, HEIGHT);
        //polygon.closePath();
        graphics.setColor(new Color(125, 142, 238, 90));
        graphics.fill(translucentShape);
        graphics.setColor(new Color(125, 142, 238));
        graphics.setStroke(new BasicStroke(3));
        graphics.draw(boldPath);
    }


}
