package com.diamondfire.helpbot.sys.tag;

public enum TagProperty {
    ACTIVATOR("activator", true) {
        @Override
        public void edit(Tag tag, String newValue) {
            tag.setActivator(newValue);
        }
    },
    TITLE("title", true) {
        @Override
        public void edit(Tag tag, String newValue) {
            tag.setTitle(newValue);
        }
    },
    RESPONSE("response", true) {
        @Override
        public void edit(Tag tag, String newValue) {
            tag.setResponse(newValue);
        }
    },
    AUTHOR_ID("authorId", false) {
        @Override
        public void edit(Tag tag, String newValue) {
            throw new UnsupportedOperationException("The `" + this.getProperty() + "` property cannot be modified.");
        }
    },
    IMAGE("image", true) {
        @Override
        public void edit(Tag tag, String newValue) {
            tag.setImage(newValue);
        }
    };
    
    private final String property;
    private final boolean modifiable;
    
    TagProperty(String property, boolean modifiable) {
        this.property = property;
        this.modifiable = modifiable;
    }
    
    public static TagProperty getByProperty(String prop) {
        for (TagProperty tagProperty : TagProperty.values()) {
            if (tagProperty.getProperty().equals(prop)) return tagProperty;
        }
        return null;
    }
    
    public String getProperty() {
        return property;
    }
    
    public boolean isModifiable() {
        return modifiable;
    }
    
    public abstract void edit(Tag tag, String newValue);
}
