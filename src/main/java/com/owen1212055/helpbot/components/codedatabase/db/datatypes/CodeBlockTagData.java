package com.owen1212055.helpbot.components.codedatabase.db.datatypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockTagData {

    private JsonObject data;


    public CodeBlockTagData(JsonObject data) {
        this.data = data;
    }

    /**
     * Parses a list of raw tag data and converts them into tag objects.
     *
     * @param data5 An array list of JsonObjects to parse into codeblock tag ArrayList.
     * @return A list of tag objects that was the raw data.
     */
    public static CodeBlockTagData[] parse(JsonArray data5) {
        List<CodeBlockTagData> info = new ArrayList<>();
        for (JsonElement er : data5) {
            info.add(new CodeBlockTagData(er.getAsJsonObject()));
        }
        return info.toArray(new CodeBlockTagData[]{});
    }

    /**
     * @return The name of the tag.
     */
    public String getName() {
        return this.data.get("name").getAsString();
    }

    /**
     * @return The options that the tag has.
     */
    public String[] getOptions() {
        List<String> option = new ArrayList<>();
        for (JsonElement info : data.get("options").getAsJsonArray()) {
            option.add(new CodeBlockTagOption(info.getAsJsonObject()).getName());
        }
        return option.toArray(new String[]{});
    }

    public CodeBlockTagOption[] getTagChoices() {
        List<CodeBlockTagOption> option = new ArrayList<>();
        for (JsonElement info : data.get("options").getAsJsonArray()) {
            option.add(new CodeBlockTagOption(info.getAsJsonObject()));
        }
        return option.toArray(new CodeBlockTagOption[]{});
    }

    /**
     * @return The main icon of the tag.
     */
    public DisplayIconData getItem() {
        return new DisplayIconData((JsonObject) this.data.get("icon"));
    }

    /**
     * @return What the tag is set to on default.
     */
    public String getDefaultValue() {
        return this.data.get("defaultOption").getAsString();
    }

}

class CodeBlockTagOption {

    private JsonObject optionData;

    CodeBlockTagOption(JsonObject optionData) {
        this.optionData = optionData;
    }

    public String getName() {
        return optionData.get("name").getAsString();
    }

    public DisplayIconData getIcon() {
        return new DisplayIconData(optionData.get("icon").getAsJsonObject());
    }
}