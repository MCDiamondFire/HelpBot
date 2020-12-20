package com.diamondfire.helpbot.bot.command.help;

public enum CommandCategory {
    
    PLAYER_STATS("Player Statistics", "These commands are made to be able to quickly search information on a player", "\uD83D\uDC64"),
    GENERAL_STATS("General Statistics", "These commands are made to be able to quickly search information on a plot or how many people are online a node.", "\uD83D\uDCCB"),
    SUPPORT("Support", "These commands are support related and are intended for staff use.", "\uD83D\uDD16"),
    CODE_BLOCK("Coding Help", "These commands are made to help you find things in dev mode.", "\uD83D\uDDA5"),
    OTHER("Other", "These commands are just random commands.", "\uD83E\uDDE9");
    
    private final String name;
    private final String description;
    private final String emoji;
    
    CommandCategory(String name, String description, String emoji) {
        this.name = name;
        this.description = description;
        this.emoji = emoji;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getEmoji() {
        return emoji;
    }
}
