package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;


import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.util.*;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.Arrays;

public interface IconEmbedBuilder<T extends CodeObject> extends CodeDisplayBuilder<T> {
    
    default void generateInfo(T data, EmbedBuilder builder) {
        DisplayIcon icon = data.getItem();
        if (icon.getAdditionalInfo().size() != 0) {
            StringBuilder additionalInfo = new StringBuilder();
            
            // Contains arrays of strings, so loop through those.
            for (JsonElement current : icon.getAdditionalInfo()) {
                JsonArray currentArray = current.getAsJsonArray();
                String[] info = Util.fromJsonArray(currentArray);
                additionalInfo.append(String.format("\n> %s ", info[0]));
                additionalInfo.append(String.join(" ", Arrays.copyOfRange(info, 1, info.length)));
                
            }
            
            builder.addField("Additional Info", MarkdownSanitizer.escape(additionalInfo.toString()), false);
        }
        
        if (icon.getWorksWith().length != 0) {
            builder.addField("Works With", StringUtil.listView("> ", true, icon.getWorksWith()), false);
        }
        // We need a 0 width space because stupid discord would format things incorrectly.
        if (icon.getExample().length != 0) {
            builder.addField("Examples", StringUtil.listView("", true, icon.getExample()), false);
        }
        if (icon.isCancellable()) {
            builder.addField("\uD83D\uDEAB Cancellable", "", false);
        }
        if (icon.isCancelledAutomatically()) {
            builder.addField("\uD83D\uDEAB Cancels Automatically", "", false);
        }
        if (icon.mobsOnly()) {
            builder.addField("Mobs Only", "", false);
        }
        
        if (icon.advanced()) {
            builder.addField("Advanced", "", false);
        }
        
    }
    
    enum ParamConverter {
        ANY_TYPE("Any Value"),
        TEXT("String"),
        COMPONENT("Styled Text"),
        NUMBER("Number"),
        LOCATION("Location"),
        VECTOR("Vector"),
        SOUND("Sound"),
        PARTICLE("Particle"),
        POTION("Potion"),
        VARIABLE("Variable"),
        LIST("List"),
        DICT("Dictionary"),
        ITEM("Item"),
        PROJECTILE("Projectile"),
        SPAWN_EGG("Spawn Egg"),
        ENTITY_TYPE("Entity Type"),
        VEHICLE("Vehicle"),
        BLOCK("Block"),
        BLOCK_TAG("Block Tag"),
        NONE("None"),
        UNKNOWN("?");
        
        private final String textName;
        
        ParamConverter(String textName) {
            this.textName = textName;
        }
        
        public static ParamConverter getTypeFromString(String internalName) {
            return ParamConverter.valueOf(internalName);
        }
        
        public String getText() {
            return textName;
        }
    }
    
}
