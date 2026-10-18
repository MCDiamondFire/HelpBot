package com.diamondfire.helpbot.df.codeinfo.viewables.embeds;


import com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes.*;
import com.diamondfire.helpbot.df.codeinfo.viewables.BasicReaction;
import com.diamondfire.helpbot.util.*;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.*;

import java.util.*;

public class CodeActionEmbedBuilder implements IconEmbedBuilder<ActionData> {
    
    @Override
    public EmbedBuilder buildDataEmbed(ActionData data) {
        CustomEmoji emote = data.getCodeBlockData().getCodeblockEnum().getEmoji();
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(data.getCodeBlockData().getCodeblockEnum().getColor())
                .setAuthor(StringUtil.smartCaps(data.getCodeblockName()), null, emote.getImageUrl());
        
        generateParameters(data, builder);
        generateInfo(data, builder);
        
        JsonObject legacyReplacement = data.getLegacyReplacement();
        if (legacyReplacement != null) {
            String replacement = legacyReplacement.get("action").getAsString();
            String text = String.join(" ", Util.fromJsonArray(legacyReplacement.getAsJsonArray("text")));
            builder.addField("**Legacy**", (replacement.isEmpty() ? "This action has been replaced." : "Replaced by **" + replacement + "**.") + (text.isEmpty() ? "" : "\n" + text), false);
        }
        
        int tagLength = data.getTags().length;
        if (tagLength != 0) {
            builder.setFooter(tagLength + " " + StringUtil.sCheck(" Tag", tagLength));
        }
        
        return builder;
    }
    
    @Override
    public LinkedHashMap<BasicReaction, CodeObject> generateDupeEmojis(List<CodeObject> dataArrayList) {
        LinkedHashMap<BasicReaction, CodeObject> dataHashed = new LinkedHashMap<>();
        for (CodeObject data : dataArrayList) {
            ActionData actionData = (ActionData) data;
            dataHashed.put(new BasicReaction(actionData.getCodeBlockData().getCodeblockEnum().getEmoji()), data);
        }
        
        return dataHashed;
    }
    
    private void generateParameters(CodeObject data, EmbedBuilder builder) {
        StringBuilder params = new StringBuilder();
        DisplayIcon icon = data.getItem();
        
        for (DisplayIcon.Argument arg : icon.getParameters()) {
            params.append("\n");
            if (arg.getText() != null) {
                params.append(arg.getText());
                continue;
            }
            
            String[] description = arg.getDescription();
            String line = String.format("**%s**", ParamConverter.getTypeFromString(arg.getType()).getText()) +
                    (arg.isPlural() ? "(s)" : "") +
                    (arg.isOptional() ? "*" : "") +
                    (description.length == 0 ? "" : " - " + description[0] + " "); // If there is a description on the argument, first part appears in same line.
            
            params.append(line);
            
            // Generate the description of the argument (skip the first index)
            if (description.length > 1) {
                params.append(String.join(" ", Arrays.copyOfRange(description, 1, description.length)));
            }
            
            // Generate extra notes that appear under the argument.
            params.append(StringUtil.listView("> ", true, arg.getExtraNotes()));
            
        }
        if (icon.getParameters().length == 0) {
            params.append("(None)");
        }
        
        String name = "<:c_chest:988240071960461402> Parameters";
        StringBuilder field = new StringBuilder();
        for (String line : params.toString().split("\n")) {
            if (!field.isEmpty() && field.length() + line.length() + 1 > MessageEmbed.VALUE_MAX_LENGTH) {
                builder.addField(name, field.toString(), false);
                name = "\u200B";
                field.setLength(0);
            }
            
            field.append(field.isEmpty() ? "" : "\n").append(StringUtil.trim(line, MessageEmbed.VALUE_MAX_LENGTH));
        }
        
        builder.addField(name, field.toString(), false);
    }
}
