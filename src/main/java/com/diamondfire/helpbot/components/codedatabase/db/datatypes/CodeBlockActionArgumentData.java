package com.diamondfire.helpbot.components.codedatabase.db.datatypes;

import com.diamondfire.helpbot.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockActionArgumentData {

    private final JsonObject data;

    /**
     * An argument that shows what variable type is used in that slot.
     *
     * @param data The information
     */
    public CodeBlockActionArgumentData(JsonObject data) {
        this.data = data;
    }

    /**
     * THis converts raw Argument data into a list of Argument objects.
     *
     * @param data The data to be parsed into an ArrayList of arguments.
     * @return A list of arguments.
     */
    public static List<CodeBlockActionArgumentData> parse(JsonArray data) {
        if (data == null) {
            return new ArrayList<>();
        }
        List<CodeBlockActionArgumentData> info = new ArrayList<>();
        for (JsonElement er : data.getAsJsonArray()) {
            info.add(new CodeBlockActionArgumentData(er.getAsJsonObject()));
        }
        return info;
    }

    /**
     * @return The type of variable that the parameter requires.
     */
    public String getType() {
        return this.data.get("type").getAsString();
    }

    /**
     * @return If this argument uses multiple of this parameter.
     */
    public boolean isPlural() {
        return this.data.get("plural").getAsBoolean();
    }

    /**
     * @return If this argument is needed or not.
     */
    public boolean isOptional() {
        return this.data.get("optional").getAsBoolean();
    }

    /**
     * @return The description of this argument.
     */
    public String[] getDescription() {
        return Util.jsonArrayToString(this.data.get("description").getAsJsonArray());
    }

    /**
     * @return Extra notes of this argument.
     */
    public String[] getExtraNotes() {
        return Util.jsonArrayToString(this.data.get("extraNotes").getAsJsonArray());
    }

    /**
     * @return If there is any text as the argument.
     */
    public String getText() {
        return this.data.get("text") == null ? null : this.data.get("text").getAsString();
    }


}
