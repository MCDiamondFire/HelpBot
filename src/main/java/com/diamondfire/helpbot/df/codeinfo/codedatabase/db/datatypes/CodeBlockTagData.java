package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.google.gson.*;
import com.diamondfire.helpbot.util.Util;

import java.util.*;

public class CodeBlockTagData {
    
    private final JsonObject data;
    
    public CodeBlockTagData(JsonObject data) {
        this.data = data;
    }
    
    public static CodeBlockTagData[] parseTags(JsonArray tags) {
        List<CodeBlockTagData> options = new ArrayList<>();
        for (JsonElement tag : tags) {
            options.add(new CodeBlockTagData(tag.getAsJsonObject()));
        }
        
        return options.toArray(new CodeBlockTagData[]{});
    }
    
    public String getName() {
        return data.get("name").getAsString();
    }
    
    public String[] getStringOptions() {
        List<String> options = new ArrayList<>();
        for (CodeBlockTagOption option : getOptions()) {
            options.add(option.getName());
        }
        
        return options.toArray(new String[]{});
    }
    
    public CodeBlockTagOption[] getOptions() {
        List<CodeBlockTagOption> option = new ArrayList<>();
        for (JsonElement info : data.get("options").getAsJsonArray()) {
            option.add(new CodeBlockTagOption(info.getAsJsonObject()));
        }
        
        return option.toArray(new CodeBlockTagOption[]{});
    }
    
    public String[] getAliases() {
        return Util.fromJsonArray(data.get("aliases").getAsJsonArray());
    }
    
    public DisplayIcon getItem() {
        return new DisplayIcon((JsonObject) data.get("icon"));
    }
    
    public String getDefaultValue() {
        return data.get("defaultOption").getAsString();
    }
    
    public JsonObject getJson() {
        return data;
    }
    
    public static class CodeBlockTagOption {
        
        private final JsonObject optionData;
        
        CodeBlockTagOption(JsonObject optionData) {
            this.optionData = optionData;
        }
        
        public String getName() {
            return optionData.get("name").getAsString();
        }
        
        public DisplayIcon getIcon() {
            return new DisplayIcon(optionData.get("icon").getAsJsonObject());
        }
    
        public JsonObject getJson() {
            return optionData;
        }
    }
    
}
