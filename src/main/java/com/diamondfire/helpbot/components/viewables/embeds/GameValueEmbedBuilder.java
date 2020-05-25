package com.diamondfire.helpbot.components.viewables.embeds;

import com.diamondfire.helpbot.components.codedatabase.db.datatypes.DisplayIconData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.GameValueData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.util.ParamConverter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class GameValueEmbedBuilder extends IconEmbedBuilder {

    @Override
    protected EmbedBuilder buildDataEmbed(SimpleData data) {
        GameValueData codeBlockData = (GameValueData) data;
        DisplayIconData item = data.getItem();

        EmbedBuilder builder = new EmbedBuilder();

        generateInfo(codeBlockData, builder);


        builder.addField("Returns Value",
                String.format("**%s** - ", ParamConverter.getTypeFromString(item.getReturnType()).getText())
                        + String.join(" ", item.getReturnDescription()),
                false);

        builder.setColor(Color.decode("#E6C78C"));

        return builder;


    }

}
