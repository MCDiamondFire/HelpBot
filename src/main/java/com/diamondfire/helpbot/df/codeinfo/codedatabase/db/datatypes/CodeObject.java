package com.diamondfire.helpbot.df.codeinfo.codedatabase.db.datatypes;

import com.diamondfire.helpbot.df.codeinfo.viewables.constants.DataTypes;
import com.google.gson.JsonObject;

public abstract class CodeObject {
    
    protected final JsonObject data;
    protected final String name;
    
    public CodeObject(JsonObject data, String name) {
        this.data = data;
        this.name = name;
    }
    
    public DisplayIcon getItem() {
        return new DisplayIcon(data.get("icon").getAsJsonObject());
    }
    
    public String getName() {
        return name;
    }
    
    public abstract DataTypes getEnum();
    
    public JsonObject getJson() {
        return data;
    }
    
}
