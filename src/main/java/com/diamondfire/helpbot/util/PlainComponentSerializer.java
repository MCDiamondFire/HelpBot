package com.diamondfire.helpbot.util;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class PlainComponentSerializer implements ComponentSerializer<Component, TextComponent, String> {
    
    /**
     * A component serializer for plain-based serialization and deserialization.
     */
    public static final PlainComponentSerializer INSTANCE = new PlainComponentSerializer();
    private final Function<TranslatableComponent, String> translatable;
    
    public PlainComponentSerializer() {
        this(component -> "");
    }
    
    public PlainComponentSerializer(final @NonNull Function<TranslatableComponent, String> translatable) {
        this.translatable = translatable;
    }
    
    @Override
    public @NonNull TextComponent deserialize(final @NonNull String input) {
        return Component.text(input);
    }
    
    @Override
    public @NonNull String serialize(final @NonNull Component component) {
        final StringBuilder sb = new StringBuilder();
        this.serialize(sb, component);
        return sb.toString();
    }
    
    public void serialize(final @NonNull StringBuilder sb, final @NonNull Component component) {
        if (component instanceof TextComponent) {
            sb.append(((TextComponent) component).content());
        } else if (component instanceof TranslatableComponent) {
            sb.append(this.translatable.apply((TranslatableComponent) component));
        } else {
            throw new IllegalArgumentException("Don't know how to turn " + component + " into a string");
        }
        
        for (final Component child : component.children()) {
            this.serialize(sb, child);
        }
    }
}