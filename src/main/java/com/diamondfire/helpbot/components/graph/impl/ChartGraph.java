package com.diamondfire.helpbot.components.graph.impl;


import com.diamondfire.helpbot.components.graph.graphable.GraphableEntry;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.*;

public class ChartGraph extends Graph {

    public static int WIDTH = 2000;
    public static int HEIGHT = 1300;
    public static int X_OFFSET = 70;
    public static int BORDER_WIDTH = 70;
    private final String graphName;

    public ChartGraph(String graphName) {
        this.graphName = graphName;
    }

    public static void drawCenteredString(Graphics graphics, String text, int x, int y) {
        Rectangle box = new Rectangle(x, y, 0, 0);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(text,
                (int) (box.getX() + (box.getWidth() - fontMetrics.stringWidth(text)) / 2),
                (int) (box.getY() + ((box.getHeight() - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent()));
    }

    @Override
    public void paintGraph(Graphics2D graphics) {
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
            int x = (valueIndex * (WIDTH) / maxPoints) + BORDER_WIDTH + X_OFFSET;
            tickRegistry.put(entry.getKey(), x);

            // Here we get the font metrics and current X to see if it's okay to put a tick there.
            if (currentTickBound <= x + graphics.getFontMetrics().stringWidth(entry.getKey().toString()) / 2) {
                currentTickBound = x + 220;
                renderTick(graphics, x, HEIGHT, entry.getKey().toString() + "");
            }
            valueIndex++;
        }

        Color color = graphics.getColor();
        graphics.setColor(Color.WHITE);
        drawCenteredString(graphics, graphName, (WIDTH + BORDER_WIDTH + X_OFFSET) / 2, BORDER_WIDTH / 2);
        graphics.setColor(color);

        // Get the largest amount of values.
        int largestNumber = points.values().stream()
                .max(Comparator.comparingInt(Integer::intValue))
                .orElse(0);

        // Get the total height of the graph.
        int totalHeight = HEIGHT - BORDER_WIDTH;
        currentTickBound = HEIGHT;
        for (int i = 0; i <= largestNumber; i++) {
            int y = (int) (HEIGHT - (i * (Math.ceil(totalHeight) / largestNumber)));
            sideTickRegistry.putIfAbsent(i, y);

            if (currentTickBound >= y) {
                currentTickBound = y - graphics.getFontMetrics().getHeight();
                renderSideTick(graphics, X_OFFSET + (BORDER_WIDTH / 2), y, i + "");
            }

        }

        // Here we iterate through
        Point lastPoint = null;
        GeneralPath boldPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        GeneralPath translucentShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        translucentShape.moveTo(BORDER_WIDTH + X_OFFSET, HEIGHT);
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

    private void renderTick(Graphics graphics, int x, int y, String text) {
        Color color = graphics.getColor();
        graphics.setColor(new Color(200, 200, 200, 50));
        graphics.fillRect(x, y, 2, 5);
        graphics.setColor(Color.WHITE);
        drawCenteredString(graphics, text, x, y + 30);

        graphics.setColor(color);
    }

    private void renderSideTick(Graphics graphics, int x, int y, String text) {
        Color color = graphics.getColor();
        graphics.setColor(new Color(200, 200, 200, 50));
        graphics.fillRect(x - 5, y, WIDTH + BORDER_WIDTH, 2);
        graphics.setColor(Color.WHITE);
        drawCenteredString(graphics, text, x - 16, y);
        graphics.setColor(color);
    }


}
