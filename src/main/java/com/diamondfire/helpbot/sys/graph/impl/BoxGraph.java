package com.diamondfire.helpbot.sys.graph.impl;

import com.diamondfire.helpbot.sys.graph.graphable.GraphableEntry;

import java.awt.*;
import java.util.ArrayList;

public abstract class BoxGraph implements Graph {

    static final int PICTURE_WIDTH = 2250;
    static final int GRAPH_WIDTH = 2000;
    static final int HEIGHT = 1300;
    static final int BORDER_SIZE = 70;
    static final int X_OFFSET = 70;
    private final String name;

    protected BoxGraph(String name) {
        this.name = name;
    }

    @Override
    public void paintGraph(Graphics2D graphics) {
        Color color = graphics.getColor();
        graphics.setColor(Color.WHITE);
        drawCenteredString(graphics, name, PICTURE_WIDTH / 2, BORDER_SIZE / 2);
        graphics.setColor(color);
        paintInner(graphics);
    }

    abstract void paintInner(Graphics2D graphics2D);

    private final ArrayList<GraphableEntry<?>> entries = new ArrayList<>();

    public ArrayList<GraphableEntry<?>> getEntries() {
        return entries;
    }

    protected void renderTick(Graphics graphics, int x, int y, String text) {
        Color color = graphics.getColor();
        graphics.setColor(new Color(200, 200, 200, 50));
        graphics.fillRect(x, y, 2, 5);
        graphics.setColor(Color.WHITE);
        drawCenteredString(graphics, text, x, y + 30);

        graphics.setColor(color);
    }

    protected void renderSideTick(Graphics graphics, int x, int y, String text) {
        Color color = graphics.getColor();
        graphics.setColor(new Color(200, 200, 200, 50));
        graphics.fillRect(x - 5, y, GRAPH_WIDTH + BORDER_SIZE, 2);
        graphics.setColor(Color.WHITE);
        drawCenteredString(graphics, text, x - 16, y);
        graphics.setColor(color);
    }

    protected void drawCenteredString(Graphics graphics, String text, int x, int y) {
        Rectangle box = new Rectangle(x, y, 0, 0);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        graphics.drawString(text,
                (int) (box.getX() + (box.getWidth() - fontMetrics.stringWidth(text)) / 2),
                (int) (box.getY() + ((box.getHeight() - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent()));
    }
}
