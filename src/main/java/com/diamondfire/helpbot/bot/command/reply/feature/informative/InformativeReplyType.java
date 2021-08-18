package com.diamondfire.helpbot.bot.command.reply.feature.informative;

import org.jetbrains.annotations.Nullable;

import java.awt.*;

public enum InformativeReplyType {
    SUCCESS("Success!", new Color(46, 204, 113)),
    INFO("Notice!", null),
    ERROR("Error!", new Color(231, 76, 60));
    
    private final String title;
    private final Color color;
    
    InformativeReplyType(@Nullable String title, @Nullable Color color) {
        this.title = title;
        this.color = color;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Color getColor() {
        return color;
    }
}
