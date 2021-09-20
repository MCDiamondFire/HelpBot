package com.diamondfire.helpbot.util;

import com.google.gson.*;

import java.util.ArrayList;

public class UserProfile {
    
    /**
     * refer to the json from
     * https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape
     * with name history from
     * https://wiki.vg/Mojang_API#UUID_to_Name_History
     * in the key 'name_history'
     * so like
     * {
     *      "name":"poopenfarten",
     *      "id":"gwrrghowenheartjoiu",
     *      "name_history":[names...],
     *      "properties": [bla bla properties textures and stuff]
     * }
     */
    
    public String uuid;
    public String username;
    public ArrayList<Pair<String, Long>> names;
    public JsonArray properties;
    
    public UserProfile(String uuid, String username, ArrayList<Pair<String, Long>> names, JsonArray properties) {
        this.uuid = uuid;
        this.username = username;
        this.names = names;
        this.properties = properties;
    }
    
    public static UserProfile fromJson(JsonObject json) {
        ArrayList<Pair<String, Long>> names = new ArrayList<>();
        for(JsonElement jsonElement : json.get("name_history").getAsJsonArray()) names.add(new Pair(jsonElement.getAsJsonObject().get("name").getAsString(), jsonElement.getAsJsonObject().get("changedToAt") == null ? null : jsonElement.getAsJsonObject().get("changedToAt").getAsLong()));
        return new UserProfile(json.get("id").getAsString(), json.get("name").getAsString(), names, json.get("properties").getAsJsonArray());
    }
    
    public String getUuid() {
        return uuid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public ArrayList<Pair<String, Long>> getNameHistory() {
        return names;
    }
    
    public JsonArray getProperties() {
        return properties;
    }
}
