package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;


import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class CodeBlockEmbedBuilder implements IconEmbedBuilder<CodeBlockData> {

    @Override
    public EmbedBuilder buildDataEmbed(CodeBlockData data) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(data.getCodeblockEnum().getColor());
        generateInfo(data, builder);

        ActionData associatedAction = data.getAssociatedAction();
        if (associatedAction != null) {
            int tagLength = associatedAction.getTags().length;
            if (tagLength != 0) {
                builder.setFooter(tagLength + StringUtil.sCheck(" Tag", tagLength));
            }

        }

        return builder;
    }
}
