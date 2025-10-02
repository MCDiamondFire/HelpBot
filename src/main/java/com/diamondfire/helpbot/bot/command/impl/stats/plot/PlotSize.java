package com.diamondfire.helpbot.bot.command.impl.stats.plot;

enum PlotSize {
    BASIC(51),
    LARGE(101),
    MASSIVE(301),
    MEGA(1001);
    
    private final int size;
    
    PlotSize(int size) {
        this.size = size;
    }
    
    public static PlotSize fromID(int id) {
        return PlotSize.values()[id];
    }
    
    public int getSize() {
        return size;
    }
}