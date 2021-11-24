package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.util.*;
import com.google.gson.*;

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
        return Util.fromJsonArray(icon.get("description").getAsJsonArray());
    }
    
    public String[] getExample() {
        return Util.fromJsonArray(icon.get("example").getAsJsonArray());
    }
    
    public String[] getWorksWith() {
        return Util.fromJsonArray(icon.get("worksWith").getAsJsonArray());
    }
    
    // Returns nested arrays
    public JsonArray getAdditionalInfo() {
        return icon.get("additionalInfo").getAsJsonArray();
    }
    
    public String getRequiredRank() {
        return icon.get("requiredRank").getAsString();
    }
    
    public boolean requireTokens() {
        return icon.get("requireTokens").getAsBoolean();
    }
    
    public boolean requiresRankOrTokens() {
        return icon.get("requireRankAndTokens").getAsBoolean();
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
        return Util.fromJsonArray(icon.get("returnDescription").getAsJsonArray());
    }
    
    public String getHead() {
        if (icon.get("head") == null) {
            return null;
        }
        JsonObject headUrl = JsonParser.parseString(new String(CompressionUtil.fromBase64(icon.get("head").getAsString().getBytes()))).getAsJsonObject();
        
        return Util.getPlayerHead(headUrl.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString().substring(38));
    }
    
    public boolean advanced() {
        return icon.get("advanced").getAsBoolean();
    }
    
    public String[] getDeprecationNote() {
        return Util.fromJsonArray(icon.getAsJsonArray("deprecatedNote"));
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
            return Util.fromJsonArray(data.get("description").getAsJsonArray());
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
                    strings.addAll(Arrays.asList(Util.fromJsonArray(element.getAsJsonArray())));
                }
            } else {
                return Util.fromJsonArray(data.get("notes").getAsJsonArray());
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
