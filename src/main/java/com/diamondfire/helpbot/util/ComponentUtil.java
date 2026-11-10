package com.diamondfire.helpbot.util;

import net.dv8tion.jda.api.components.container.*;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.*;

import java.util.*;

public class ComponentUtil {

    private static final int MAX_TEXT_LENGTH = 4000;

    public static @Nullable Container toContainer(@NotNull MessageEmbed embed, @Nullable Thumbnail thumbnail) {
        List<ContainerChildComponent> components = new ArrayList<>();
        int length = 0;

        StringBuilder header = new StringBuilder();
        if (embed.getTitle() != null) {
            header.append("## ").append(embed.getTitle());
        }
        if (embed.getDescription() != null && !embed.getDescription().isBlank()) {
            header.append(header.isEmpty() ? "" : "\n").append(embed.getDescription());
        }

        if (!header.isEmpty()) {
            TextDisplay headerDisplay = TextDisplay.of(header.toString());
            components.add(thumbnail == null ? headerDisplay : Section.of(thumbnail, headerDisplay));
            length += header.length();
        }

        List<String> fields = new ArrayList<>();
        for (MessageEmbed.Field field : embed.getFields()) {
            String name = field.getName() == null ? "" : field.getName();
            String value = field.getValue() == null ? "" : field.getValue();
            fields.add(value.isBlank() ? "**" + name + "**" : "**" + name + "**\n" + value);
        }

        if (!fields.isEmpty()) {
            String fieldText = String.join("\n\n", fields);
            components.add(Separator.createDivider(Separator.Spacing.SMALL));
            components.add(TextDisplay.of(fieldText));
            length += fieldText.length();
        }

        if (embed.getFooter() != null && embed.getFooter().getText() != null && !embed.getFooter().getText().isBlank()) {
            String footer = "-# " + embed.getFooter().getText();
            components.add(TextDisplay.of(footer));
            length += footer.length();
        }

        if (components.isEmpty() || length > MAX_TEXT_LENGTH) {
            return null;
        }

        Container container = Container.of(components);
        return embed.getColor() == null ? container : container.withAccentColor(embed.getColor());
    }
}
