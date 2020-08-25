package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.util.*;
import com.google.gson.*;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class DisplayIcon {

    private final JsonObject icon;

    public DisplayIcon(JsonObject icon) {
        this.icon = icon;
    }

    public String getItemName() {
        return icon.get("name").getAsString();
    }

    public String getMaterial() {
        return icon.get("material").getAsString();
    }

    public String[] getDescription() {
        return Util.jsonArrayToString(icon.get("description").getAsJsonArray());
    }

    public String[] getExample() {
        return Util.jsonArrayToString(icon.get("example").getAsJsonArray());
    }

    public String[] getWorksWith() {
        return Util.jsonArrayToString(icon.get("worksWith").getAsJsonArray());
    }

    // Returns nested arrays
    public JsonArray getAdditionalInfo() {
        return icon.get("additionalInfo").getAsJsonArray();
    }

    public String getRequiredRank() {
        return icon.get("requiredRank").getAsString();
    }

    public boolean requiresCredits() {
        return icon.get("requireCredits").getAsBoolean();
    }

    public boolean requiresRankOrCredits() {
        return icon.get("requireRankAndCredits").getAsBoolean();
    }

    public Argument[] getParameters() {
        if (icon.get("arguments") == null) return new Argument[]{};

        return Argument.parseArguments(icon.get("arguments").getAsJsonArray()).toArray(new Argument[]{});
    }

    //mobsOnly can be null if it isn't an action icon.
    public boolean mobsOnly() {
        return icon.get("mobsOnly") != null && icon.get("mobsOnly").getAsBoolean();
    }

    //isCancellable can be null if it isn't an event icon.
    public boolean isCancellable() {
        return icon.get("cancellable") != null && icon.get("cancellable").getAsBoolean();
    }

    //isCancelledAutomatically can be null if it isn't an event icon.
    public boolean isCancelledAutomatically() {
        return icon.get("cancelledAutomatically") != null && icon.get("cancelledAutomatically").getAsBoolean();
    }

    //getReturnType can be null if it isn't a game value icon.
    public String getReturnType() {
        return icon.get("returnType").getAsString();
    }

    //getReturnDescription can be null if it isn't a game value icon.
    public String[] getReturnDescription() {
        return Util.jsonArrayToString(icon.get("returnDescription").getAsJsonArray());
    }


    public File getHead() {
        if (icon.get("head") == null) {
            return null;
        }
        JsonObject headUrl = JsonParser.parseString(new String(StringUtil.fromBase64(icon.get("head").getAsString().getBytes()))).getAsJsonObject();

        return Util.getPlayerHead(headUrl.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString().substring(38));
    }

    public static class Argument {

        private final JsonObject data;

        public Argument(JsonObject data) {
            this.data = data;
        }

        public static List<Argument> parseArguments(JsonArray args) {
            List<Argument> argList = new ArrayList<>();
            for (JsonElement er : args.getAsJsonArray()) {
                argList.add(new Argument(er.getAsJsonObject()));
            }

            return argList;
        }

        public String getType() {
            return data.get("type").getAsString();
        }

        public boolean isPlural() {
            return data.get("plural").getAsBoolean();
        }

        public boolean isOptional() {
            return data.get("optional").getAsBoolean();
        }

        public String[] getDescription() {
            return Util.jsonArrayToString(data.get("description").getAsJsonArray());
        }

        //BUG For some reason, extra notes is a json array inside of a json array in certain situations.
        public String[] getExtraNotes() {
            List<String> strings = new ArrayList<>();
            JsonArray array = data.get("notes").getAsJsonArray();
            if (array.size() == 0) {
                return new String[0];
            }

            if (array.get(0).isJsonArray()) {
                for (JsonElement element : array) {
                    strings.addAll(Arrays.asList(Util.jsonArrayToString(element.getAsJsonArray())));
                }
            } else {
                return Util.jsonArrayToString(data.get("notes").getAsJsonArray());
            }

            return strings.toArray(new String[0]);
        }

        public String getText() {
            JsonElement textElement = data.get("text");
            if (textElement == null) {
                return null;
            }

            return textElement.getAsString();
        }

    }

}
