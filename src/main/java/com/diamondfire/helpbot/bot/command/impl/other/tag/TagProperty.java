package com.diamondfire.helpbot.bot.command.impl.other.tag;

public enum TagProperty {
    ACTIVATOR("activator", true),
    TITLE("title", true),
    RESPONSE("response", true),
    AUTHOR_ID("authorId", false),
    IMAGE("image", true);
    
    private final String property;
    private final boolean modifiable;
    
    TagProperty(String property, boolean modifiable) {
        this.property = property;
        this.modifiable = modifiable;
    }
    
    public String getProperty() {
            return property;
        }
        
    public boolean isModifiable() {
            return modifiable;
        }
        
    public static TagProperty getByProperty(String prop) {
        for (TagProperty tagProperty : TagProperty.values()) {
            if (tagProperty.getProperty().equals(prop)) return tagProperty;
        }
        return null;
    }
}
