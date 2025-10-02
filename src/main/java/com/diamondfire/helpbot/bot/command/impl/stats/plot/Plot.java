package com.diamondfire.helpbot.bot.command.impl.stats.plot;

import java.time.LocalDate;

public record Plot(int id, String handle, String description, String name, String ownerName, int node,
                   PlotSize plotSize, String tags, int immunityLevel, LocalDate activeTime, boolean whitelisted,
                   int playerCount, int votes, int xMin, int zMin, String icon) {
    
}