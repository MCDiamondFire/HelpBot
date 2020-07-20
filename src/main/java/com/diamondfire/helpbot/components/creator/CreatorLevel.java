package com.diamondfire.helpbot.components.creator;

import com.diamondfire.helpbot.components.creator.requirements.*;

public enum CreatorLevel {
    SPARK("Spark", 734808957545349221L, new StaticRequirementProvider(0)),
    EMBER("Ember", 734808957738287114L, new StaticRequirementProvider(200)),
    BLAZE("Blaze", 734808957243097110L, new StaticRequirementProvider(1000)),
    INFERNO("Inferno", 734808957536960512L, new StaticRequirementProvider(3000)),
    QUARTZ("Quartz", 734808957604069416L, new StaticRequirementProvider(5000)),
    JADE("Jade", 734808957302079580L, new DynamicRequirementProvider("jade")),
    RUBY("Ruby", 734808957289496667L, new DynamicRequirementProvider("ruby")),
    DIAMOND("Diamond", 734808957490823228L, new DynamicRequirementProvider("diamond"));

    private final String name;
    private final long emoji;
    private final RequirementProvider requirementProvider;

    CreatorLevel(String name, long emoji, RequirementProvider requirementProvider) {
        this.name = name;
        this.emoji = emoji;
        this.requirementProvider = requirementProvider;
    }

    public static CreatorLevel getLevel(int level) {
        return values()[level];
    }

    public static CreatorLevel getNextLevel(CreatorLevel level) {
        if (level == DIAMOND) return DIAMOND;

        CreatorLevel[] levels = values();
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] == level) {
                return levels[++i];
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public RequirementProvider getRequirementProvider() {
        return requirementProvider;
    }

    public long getEmoji() {
        return emoji;
    }

    public String display() {
        return display(false);
    }
    public String display(boolean hideStar) {
        return String.format("<:%s:%s>", getName().toLowerCase(), getEmoji()) + " " + getName() + (requirementProvider instanceof DynamicRequirementProvider && !hideStar ? "*" : "");
    }
}
