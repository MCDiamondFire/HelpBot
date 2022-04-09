package com.diamondfire.helpbot.bot.command.argument.impl.types.enumtypes;

import com.diamondfire.helpbot.sys.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public enum TagProperty implements EnumArgument.InputEnum {
    
    ACTIVATOR("activator", true, Tag::setActivator),
    TITLE("title", true, Tag::setTitle),
    RESPONSE("response", true, Tag::setResponse),
    AUTHOR_ID("authorId", false, (tag, newValue) -> {
        throw new UnsupportedOperationException();
    }),
    IMAGE("image", true, Tag::setImage)
    ;
    
    private final String name;
    private final boolean modifiable;
    private final BiConsumer<Tag, String> modifier;
    
    TagProperty(String name, boolean modifiable, BiConsumer<Tag, String> modifier) {
        this.name = name;
        this.modifiable = modifiable;
        this.modifier = modifier;
    }
    
    public static @NotNull TagProperty of(String property) {
        for (TagProperty tagProperty : TagProperty.values()) {
            if (tagProperty.getName().equals(property)) {
                return tagProperty;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isHidden() {
        return !isModifiable();
    }
    
    public boolean isModifiable() {
        return modifiable;
    }
    
    public void set(Tag tag, String newValue) {
        getModifier().accept(tag, newValue);
    }
    
    public BiConsumer<Tag, String> getModifier() {
        return modifier;
    }
}