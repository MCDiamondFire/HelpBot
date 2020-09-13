package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.CodeObject;
import net.dv8tion.jda.api.EmbedBuilder;

public class SimpleIconBuilder implements IconEmbedBuilder<CodeObject> {

    @Override
    public EmbedBuilder buildDataEmbed(CodeObject data) {
        EmbedBuilder builder = new EmbedBuilder();
        generateInfo(data, builder);

        return builder;
    }
}
