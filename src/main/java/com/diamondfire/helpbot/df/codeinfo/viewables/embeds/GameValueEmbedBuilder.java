package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class GameValueEmbedBuilder extends IconEmbedBuilder {

    @Override
    protected EmbedBuilder buildDataEmbed(CodeObject data) {
        GameValueData codeBlockData = (GameValueData) data;
        DisplayIcon item = data.getItem();
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
