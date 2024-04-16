package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;


import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.*;

public interface CodeDisplayBuilder<T extends CodeObject> {
    
    default EmbedBuilder generateEmbed(T data) {
        DisplayIcon icon = data.getItem();
        EmbedBuilder builder = buildDataEmbed(data)
                .setDescription(String.join(" ", icon.getDescription()))
                .setThumbnail("attachment://" + icon.getMaterial().toLowerCase() + ".png");
        
        String iconName = icon.getItemName();
        String dataName = data.getName();
        if (!iconName.equals(dataName)) {
            builder.setTitle(iconName + " | " + dataName);
        } else {
            builder.setTitle(dataName);
        }
        
        List<String> tokens = new ArrayList<>();
        String footerText = builder.build().getFooter() == null ? null : builder.build().getFooter().getText();
        if (footerText != null) {
            tokens.add(footerText);
        }
        
        if (icon.requireTokens() && !icon.getRequiredRank().equals("")) {
            tokens.add("Unlock with Tokens OR " + icon.getRequiredRank());
        } else if (icon.requireTokens()) {
            tokens.add("Unlock with Tokens");
        } else if (!icon.getRequiredRank().equals("")) {
            tokens.add("Unlock with " + icon.getRequiredRank());
        }
        
        String[] deprecatedNote = icon.getDeprecationNote();
        if (deprecatedNote.length != 0) {
            builder.addField("**Deprecated**", String.join(" ", deprecatedNote), false);
        }
        
        builder.setFooter(String.join(" | ", tokens));
        
        return builder;
    }
    
    EmbedBuilder buildDataEmbed(T data);
    
    default LinkedHashMap<BasicReaction, CodeObject> generateDupeEmojis(List<CodeObject> dataArrayList) {
        if (dataArrayList.size() > 10) {
            throw new IllegalStateException("Not enough emojis to map 10 objects!");
        }
        Deque<String> nums = Util.getUnicodeNumbers();
        LinkedHashMap<BasicReaction, CodeObject> dataHashed = new LinkedHashMap<>();
        for (CodeObject data : dataArrayList) {
            dataHashed.put(new BasicReaction(Emoji.fromUnicode(nums.pop())), data);
        }
        
        return dataHashed;
    }
}
