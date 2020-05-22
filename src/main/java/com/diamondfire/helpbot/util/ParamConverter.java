package com.diamondfire.helpbot.util;

public enum ParamConverter {
    ANY_TYPE("Any Variables"),
    TEXT("Text"),
    NUMBER("Number"),
    LOCATION("Location"),
    SOUND("Sound"),
    PARTICLE("Particle"),
    POTION("Potion"),
    VARIABLE("Dynamic Variable"),
    LIST("List"),
    ITEM("Item"),
    PROJECTILE("Projectile"),
    SPAWN_EGG("Spawn egg"),
    ENTITY_TYPE("Entity Type"),
    VEHICLE("Vehicle"),
    BLOCK("Block"),
    BLOCK_TAG("Block Tag"),
    UNKNOWN("?");

    private final String textName;

    ParamConverter(String textName) {
        this.textName = textName;
    }

    public static ParamConverter getTypeFromString(String internalName) {
        ParamConverter.valueOf(internalName);
        return ParamConverter.valueOf(internalName);

    }

    public String getText() {
        return textName;
    }
}
