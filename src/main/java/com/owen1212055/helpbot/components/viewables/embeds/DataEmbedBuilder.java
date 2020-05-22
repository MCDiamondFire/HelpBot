package com.owen1212055.helpbot.components.viewables.embeds;

import com.owen1212055.helpbot.components.codedatabase.db.datatypes.DisplayIconData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.components.viewables.BasicReaction;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class DataEmbedBuilder {

    public EmbedBuilder generateEmbed(SimpleData data) {
        EmbedBuilder builder = buildDataEmbed(data);


        DisplayIconData icon = data.getItem();

        builder.setTitle(icon.getItemName() + " | " + data.getMainName());

        builder.setDescription(String.join(" ", icon.getDescription()));

        String footerText = builder.build().getFooter() == null ? "" : builder.build().getFooter().getText();

        StringBuilder footer = new StringBuilder(footerText);

        if (icon.getRequiredCredits() && !icon.getRequiredRank().equals("Default")) {
            if (footerText.length() != 0)  footer.append(" | ");
            footer.append("Unlock with Credits OR ").append(icon.getRequiredRank());
        } else if (icon.getRequiredCredits()) {
            if (footerText.length() != 0)  footer.append(" | ");
            footer.append("Unlock with Credits");
        } else if (!icon.getRequiredRank().equals("Default")) {
            if (footerText.length() != 0)  footer.append(" | ");
            footer.append("Unlock with " + icon.getRequiredRank());
        }


        builder.setFooter(footer.toString());

        String material = icon.getMaterial().toLowerCase();

        builder.setThumbnail("attachment://" + material + ".png");


        return builder;
    }

    abstract protected EmbedBuilder buildDataEmbed(SimpleData data);

    public LinkedHashMap<BasicReaction, SimpleData> generateDupeEmojis(List<SimpleData> dataArrayList) throws IllegalArgumentException {
        LinkedList<String> nums = new LinkedList<>();
        nums.add("\u0031\uFE0F\u20E3");
        nums.add("\u0032\uFE0F\u20E3");
        nums.add("\u0033\uFE0F\u20E3");
        nums.add("\u0034\uFE0F\u20E3");
        nums.add("\u0035\uFE0F\u20E3");
        nums.add("\u0036\uFE0F\u20E3");
        nums.add("\u0037\uFE0F\u20E3");
        nums.add("\u0038\uFE0F\u20E3");
        nums.add("\u0039\uFE0F\u20E3");
        nums.add("\uD83D\uDD1F");
        nums.add("\uD83D\uDE42");

        if (dataArrayList.size() > 10) {
            throw new IllegalArgumentException("Not enough emojis to map 10 objects!");
        }
        LinkedHashMap<BasicReaction, SimpleData> dataHashed = new LinkedHashMap<>();
        for (SimpleData data : dataArrayList) {
            dataHashed.put(new BasicReaction(nums.pop()), data);
        }

        return dataHashed;
    }
}
