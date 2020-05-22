package com.owen1212055.helpbot.components.viewables.embeds;

import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.CodeBlockTagData;
import com.owen1212055.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.owen1212055.helpbot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

public class CodeBlockEmbedBuilder extends IconEmbedBuilder {

    @Override
    protected EmbedBuilder buildDataEmbed(SimpleData data) {
        CodeBlockData codeBlockData = (CodeBlockData) data;

        EmbedBuilder builder = new EmbedBuilder();

        generateInfo(data, builder);


        if (codeBlockData.getAssociatedAction() != null) {
            CodeBlockTagData[] tags = codeBlockData.getAssociatedAction().getTags();

            StringBuilder footer = new StringBuilder();

            if (tags.length != 0) {
                footer.append(tags.length + Util.sCheck(" Tag", tags.length) );
            }

            builder.setFooter(footer.toString());
        }
        builder.setColor(codeBlockData.getCodeblockEnum().getColor());

        return builder;


    }

}
