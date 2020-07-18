package com.diamondfire.helpbot.components.viewables.embeds;

import com.diamondfire.helpbot.components.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.components.viewables.BasicReaction;
import com.diamondfire.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public abstract class DataEmbedBuilder {

    public EmbedBuilder generateEmbed(SimpleData data) {
        EmbedBuilder builder = buildDataEmbed(data);
        DisplayIconData icon = data.getItem();

        String iconName = icon.getItemName();
        String dataName = data.getMainName();
        if (!iconName.equals(dataName)) {
            builder.setTitle(iconName + " | " + dataName);
        } else {
            builder.setTitle(dataName);
        }

        builder.setDescription(String.join(" ", icon.getDescription()));

        String footerText = builder.build().getFooter() == null ? "" : builder.build().getFooter().getText();
        StringBuilder footer = new StringBuilder(footerText);

        if (icon.getRequiredCredits() && !icon.getRequiredRank().equals("")) {
            if (footerText.length() != 0) footer.append(" | ");
            footer.append("Unlock with Credits OR ").append(icon.getRequiredRank());
        } else if (icon.getRequiredCredits()) {
            if (footerText.length() != 0) footer.append(" | ");
            footer.append("Unlock with Credits");
        } else if (!icon.getRequiredRank().equals("")) {
            if (footerText.length() != 0) footer.append(" | ");
            footer.append("Unlock with " + icon.getRequiredRank());
        }

        builder.setFooter(footer.toString());
        builder.setThumbnail("attachment://" + icon.getMaterial().toLowerCase() + ".png");

        return builder;
    }

    abstract protected EmbedBuilder buildDataEmbed(SimpleData data);

    public LinkedHashMap<BasicReaction, SimpleData> generateDupeEmojis(List<SimpleData> dataArrayList) {
        if (dataArrayList.size() > 10) {
            throw new IllegalStateException("Not enough emojis to map 10 objects!");
        }
        LinkedList<String> nums = Util.getUnicodeNumbers();
        LinkedHashMap<BasicReaction, SimpleData> dataHashed = new LinkedHashMap<>();
        for (SimpleData data : dataArrayList) {
            dataHashed.put(new BasicReaction(nums.pop()), data);
        }

        return dataHashed;
    }
}
