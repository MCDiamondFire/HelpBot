package com.diamondfire.helpbot.command.help;

public enum CommandCategory {

    STATS("Statistics", "These commands are made to be able to quickly search information on a plot or a player.", "\uD83D\uDCCB"),
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
