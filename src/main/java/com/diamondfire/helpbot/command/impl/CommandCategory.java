package com.diamondfire.helpbot.command.impl;

public enum CommandCategory {

    CODE_BLOCK("Coding Help", "These commands are made to help you find things in dev mode.", "\uD83D\uDDA5"),
    STATS("Statistics", "These commands are made to be able to quickly search information on a plot or a player.", "\uD83D\uDCC8"),
    OTHER("Other", "These commands are just random commands.", "\uD83E\uDDE9"),
    HIDDEN(null, null, null);

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
