package com.diamondfire.helpbot.components.viewables.embeds;

import com.diamondfire.helpbot.components.codedatabase.db.datatypes.CodeBlockData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.CodeBlockTagData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.util.Util;
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
                footer.append(tags.length + Util.sCheck(" Tag", tags.length));
            }

            builder.setFooter(footer.toString());
        }
        builder.setColor(codeBlockData.getCodeblockEnum().getColor());

        return builder;


    }

}
