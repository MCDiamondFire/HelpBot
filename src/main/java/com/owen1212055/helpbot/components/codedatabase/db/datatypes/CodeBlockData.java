package com.owen1212055.helpbot.components.codedatabase.db.datatypes;

import com.google.gson.JsonObject;
import com.owen1212055.helpbot.components.viewables.consts.CodeBlockEnum;
import com.owen1212055.helpbot.components.viewables.consts.DataTypes;

public class CodeBlockData extends SimpleData {

    private CodeBlockActionData assosiatedAction;

    /**
     * Creates a main codeblock that goes into the players inventory.
     *
     * @param data The information
     */
    public CodeBlockData(JsonObject data) {
        super(data, data.get("name").getAsString());
    }

    /**
     * @return The name of the codeblock.
     */
    public String getName() {
        return this.data.get("name").getAsString();
    }

    /**
     * @return The identifier of the codeblock. (internal ID)
     */
    public String getIdentifier() {
        return this.data.get("identifier").getAsString();
    }

    /**
     * @return The item of the codeblock that is in the players inventory.
     */
    @Override
    public DisplayIconData getItem() {
        return new DisplayIconData(this.data.get("item").getAsJsonObject());
    }

    public void assignAction(CodeBlockActionData data) {
        assosiatedAction = data;
    }

    public CodeBlockActionData getAssociatedAction() {
        return assosiatedAction;
    }

    public CodeBlockEnum getCodeblockEnum() {
        return CodeBlockEnum.getFromID(getIdentifier());
    }

    @Override
    public DataTypes getEnum() {
        return DataTypes.CODEBLOCK;
    }
    //    @Override
//    public EmbedBuilder buildEmbed() {
//        EmbedBuilder builder = new EmbedBuilder();
//        DisplayIconData icon = this.getIcon();
//        StringBuilder params = new StringBuilder();
//
//        if (icon.getAdditionalInfo().size() != 0) {
//            StringBuilder additionalInfo = new StringBuilder();
//
//            // Contains arrays of strings, so loop through those.
//            for (JsonElement current : icon.getAdditionalInfo()) {
//                String mainAdditional = current.getAsJsonArray().get(0).getAsString();
//
//                // Request combines stuff like "ite-rations" to "iterations". Skezza complained about this not being fixed, it's a shame he retired so he cannot see this wonderful fix..
//                boolean requestCombine = mainAdditional.endsWith("-");
//
//                mainAdditional = requestCombine ? mainAdditional.substring(0, mainAdditional.length() - 1) : (mainAdditional + " ");
//
//
//                // Prints the first part of the additional section
//                additionalInfo.append("\n\\> " + mainAdditional);
//
//                // Generate the rest of the additional info (skip the first index)
//                JsonArray array = current.getAsJsonArray();
//
//                for (int i = 1; i < array.size(); i++) {
//                    if (requestCombine) {
//                        requestCombine = false;
//                        additionalInfo.append(array.get(i).getAsString());
//                        continue;
//                    }
//                    additionalInfo.append(" " + array.get(i).getAsString());
//
//                }
//
//            }
//
//            builder.addField("Additional Info", additionalInfo.toString(), false);
//        }
//
//        if (icon.getWorksWith().length != 0) {
//            builder.addField("Works With", StringFormatting.listView(icon.getWorksWith(), "»"), false);
//        }
//
//        if (icon.getExample().length != 0) {
//            builder.addField("Examples", StringFormatting.listView(icon.getExample(), ""), false);
//        }
//
//        StringBuilder footer = new StringBuilder();
//        if (getAssociatedAction() != null) {
//            if (this.getAssociatedAction().getTags().length != 0) {
//                footer.append(this.getAssociatedAction().getTags().length + " Tags ");
//            }
//        }
//
//
//        builder.setColor(this.getEnum().getColor());
//
//        builder.setFooter(footer.toString());
//
//
//
//
//
//        return builder;
//    }

}
