package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public abstract class DataEmbedBuilder {

    public EmbedBuilder generateEmbed(CodeObject data) {
        EmbedBuilder builder = buildDataEmbed(data);
        DisplayIcon icon = data.getItem();

        String iconName = icon.getItemName();
        String dataName = data.getName();
        if (!iconName.equals(dataName)) {
            builder.setTitle(iconName + " | " + dataName);
        } else {
            builder.setTitle(dataName);
        }

        builder.setDescription(String.join(" ", icon.getDescription()));

        String footerText = builder.build().getFooter() == null ? "" : builder.build().getFooter().getText();
        StringBuilder footer = new StringBuilder(footerText);

        if (icon.requiresCredits() && !icon.getRequiredRank().equals("")) {
            if (footerText.length() != 0) footer.append(" | ");
            footer.append("Unlock with Credits OR ").append(icon.getRequiredRank());
        } else if (icon.requiresCredits()) {
            if (footerText.length() != 0) footer.append(" | ");
            footer.append("Unlock with Credits");
        } else if (!icon.getRequiredRank().equals("")) {
            if (footerText.length() != 0) footer.append(" | ");
            footer.append("Unlock with ").append(icon.getRequiredRank());
        }

        builder.setFooter(footer.toString());
        builder.setThumbnail("attachment://" + icon.getMaterial().toLowerCase() + ".png");

        return builder;
    }

    abstract protected EmbedBuilder buildDataEmbed(CodeObject data);

    public LinkedHashMap<BasicReaction, CodeObject> generateDupeEmojis(List<CodeObject> dataArrayList) {
        if (dataArrayList.size() > 10) {
            throw new IllegalStateException("Not enough emojis to map 10 objects!");
        }
        Deque<String> nums = Util.getUnicodeNumbers();
        LinkedHashMap<BasicReaction, CodeObject> dataHashed = new LinkedHashMap<>();
        for (CodeObject data : dataArrayList) {
            dataHashed.put(new BasicReaction(nums.pop()), data);
        }

        return dataHashed;
    }
}
