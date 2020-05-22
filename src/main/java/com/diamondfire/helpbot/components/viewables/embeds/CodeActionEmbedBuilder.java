package com.diamondfire.helpbot.components.viewables.embeds;

import com.diamondfire.helpbot.components.viewables.BasicReaction;
import com.diamondfire.helpbot.util.ParamConverter;
import com.diamondfire.helpbot.util.Util;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.CodeBlockActionArgumentData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.CodeBlockActionData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.DisplayIconData;
import com.diamondfire.helpbot.components.codedatabase.db.datatypes.SimpleData;
import com.diamondfire.helpbot.util.StringFormatting;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.LinkedHashMap;
import java.util.List;

public class CodeActionEmbedBuilder extends IconEmbedBuilder {

    @Override
    protected EmbedBuilder buildDataEmbed(SimpleData data) {
        CodeBlockActionData actionData = (CodeBlockActionData) data;

        EmbedBuilder builder = new EmbedBuilder();

        generateParameters(data, builder);
        generateInfo(data, builder);


        StringBuilder footer = new StringBuilder();

        if (actionData.getTags().length != 0) {
            footer.append(actionData.getTags().length + Util.sCheck(" Tag", actionData.getTags().length));
        }

        builder.setColor(actionData.getCodeBlockData().getCodeblockEnum().getColor());

        builder.setFooter(footer.toString());

        return builder;


    }

    private void generateParameters(SimpleData data, EmbedBuilder builder) {
        StringBuilder params = new StringBuilder();
        DisplayIconData icon = data.getItem();

        for (CodeBlockActionArgumentData arg : icon.getParameters()) {
            params.append("\n");
            if (arg.getText() != null) {
                params.append(arg.getText());
                continue;
            }

            String line =
                    String.format("**%s**", ParamConverter.getTypeFromString(arg.getType()).getText()) +
                            (arg.isPlural() ? "(s)" : "") +
                            (arg.isOptional() ? "*" : "") +
                            " - " + arg.getDescription()[0] + " "; // If there is a description on the argument, first part appears in same line.

            params.append(line);

            // Generate the description of the argument (skip the first index)
            for (int i = 1; i < arg.getDescription().length; i++) {
                params.append(arg.getDescription()[i]);
            }

            // Generate extra notes that appear under the argument.
            params.append(StringFormatting.listView(arg.getExtraNotes(), "> ", true));

        }
        if (icon.getParameters().length == 0) {
            params.append("(None)");
        }

        builder.addField("<:c_chest:688643661755318272> Parameters", params.toString(), false);

    }

    @Override
    public LinkedHashMap<BasicReaction, SimpleData> generateDupeEmojis(List<SimpleData> dataArrayList) {
        LinkedHashMap<BasicReaction, SimpleData> dataHashed = new LinkedHashMap<>();
        for (SimpleData data : dataArrayList) {
            CodeBlockActionData actionData = (CodeBlockActionData) data;

            dataHashed.put(new BasicReaction(actionData.getCodeBlockData().getCodeblockEnum().getEmoji()), data);
        }

        return dataHashed;
    }
}
