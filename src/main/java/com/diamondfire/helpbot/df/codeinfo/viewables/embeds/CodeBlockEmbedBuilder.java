package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;

import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class CodeBlockEmbedBuilder extends IconEmbedBuilder {

    @Override
    protected EmbedBuilder buildDataEmbed(CodeObject data) {
        CodeBlockData codeBlockData = (CodeBlockData) data;
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(codeBlockData.getCodeblockEnum().getColor());
        generateInfo(data, builder);

        if (codeBlockData.getAssociatedAction() != null) {
            int tagLength = codeBlockData.getAssociatedAction().getTags().length;
            if (tagLength != 0) {
                builder.setFooter(tagLength + StringUtil.sCheck(" Tag", tagLength));
            }

        }

        return builder;
    }

}
