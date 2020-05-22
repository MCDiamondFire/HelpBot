package com.owen1212055.helpbot.components.viewables.embeds;

import com.owen1212055.helpbot.components.codedatabase.db.datatypes.DisplayIconData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.GameValueData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.util.ParamConverter;
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
